package com.bkatwal.impl;

import com.bkatwal.annotation.CacheDelete;
import com.bkatwal.annotation.CachePut;
import com.bkatwal.annotation.Cacheable;
import com.bkatwal.api.RedisCacheService;
import com.bkatwal.util.CacheUtil;
import com.bkatwal.util.KeyGenerators;
import com.bkatwal.util.SpringExpressionParserUtil;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * created by bikas katwal on 27/12/18
 */

@Aspect
@Order(101)
@Component
@Slf4j
public class RedisCacheAspect {

  @Autowired
  RedisTemplate<String, Object> redisTemplate;

  HashOperations<String, Object, Object> hashOperations;

  @Pointcut("execution(* *(..)) && @annotation(com.bkatwal.annotation.Cacheable)")
  public void executionOfCacheableMethod() {
  }

  @Pointcut("execution(* *(..)) && @annotation(com.bkatwal.annotation.CachePut)")
  public void executionOfCachePutMethod() {
  }

  @Pointcut("execution(* *(..)) && @annotation(com.bkatwal.annotation.CacheDelete)")
  public void executionOfCacheDeleteMethod() {
  }

  @PostConstruct
  public void init() {
    hashOperations = redisTemplate.opsForHash();
  }

  @Autowired
  SpringExpressionParserUtil springExpressionParserUtil;

  @Value("${useRedisAsCache}")
  private boolean useRedisAsCache;

  @Autowired RedisCacheService redisCacheService;

  @AfterReturning(pointcut = "executionOfCachePutMethod()", returning = "returnObject")
  public void putInCache(final JoinPoint joinPoint, final Object returnObject) {

    try {
      if (returnObject == null) {
        return;
      }
      if (!useRedisAsCache) {
        return;
      }
      CachePut cachePutAnnotation = getAnnotation(joinPoint, CachePut.class);
      Object cacheKey = springExpressionParserUtil
          .parseAndGetCacheKeyFromExpression(cachePutAnnotation.keyExpression(), returnObject,
              joinPoint.getArgs(), cachePutAnnotation.keyGenerator());

      if (cachePutAnnotation.isAsync()) {
        redisCacheService.saveInRedisAsync(cachePutAnnotation.cacheNames(), cacheKey, returnObject);
      } else {
        redisCacheService.saveInRedis(cachePutAnnotation.cacheNames(), cacheKey, returnObject);
      }

    } catch (Exception e) {
      log.error("Data save failed!!!");
      //TODO uncomment below if needed, but don't see how application will handle,
      //better to keep it commented and instead trigger mail
      //throw new RedisSystemException("Data save failed!!!", e);
    }
  }

  @AfterReturning(pointcut = "executionOfCacheDeleteMethod()", returning = "returnObject")
  public void deleteCache(final JoinPoint joinPoint, final Object returnObject) {

    try {
      if (!useRedisAsCache) {
        return;
      }
      CacheDelete cacheDeleteAnnotation = getAnnotation(joinPoint, CacheDelete.class);

      String[] cacheNames = cacheDeleteAnnotation.cacheNames();
      Object cacheKey = null;
      if (!cacheDeleteAnnotation.removeAll()) {
        cacheKey = springExpressionParserUtil
            .parseAndGetCacheKeyFromExpression(cacheDeleteAnnotation.keyExpression(), returnObject,
                joinPoint.getArgs(), cacheDeleteAnnotation.keyGenerator());
      }
      if (cacheDeleteAnnotation.isAsync()) {
        if (cacheDeleteAnnotation.removeAll())
          redisCacheService.invalidateCache(cacheNames);
      } else {
        redisCacheService.invalidateCache(cacheNames, cacheKey);
      }

    } catch (Exception e) {
      log.error("Data delete failed!!!");
      //TODO uncomment below if needed, but don't see how application will handle,
      //better to keep it commented and instead trigger mail
      //throw new RedisSystemException("Data save failed!!!", e);
    }
  }

  @Around("executionOfCacheableMethod()")
  public Object getAndSaveInCache(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

    if (!useRedisAsCache) {
      return callActualMethod(proceedingJoinPoint);
    }

    Object returnObject = null;

    Cacheable cacheableAnnotation = null;
    Object cacheKey = null;
    try {
      cacheableAnnotation = getAnnotation(proceedingJoinPoint, Cacheable.class);

      KeyGenerators keyGenerator = cacheableAnnotation.keyGenerator();

      if (Strings.isNullOrEmpty(cacheableAnnotation.keyExpression())) {
        cacheKey = CacheUtil.buildCacheKey(proceedingJoinPoint.getArgs());
      } else {
        cacheKey = springExpressionParserUtil
            .parseAndGetCacheKeyFromExpression(cacheableAnnotation.keyExpression(), null,
                proceedingJoinPoint.getArgs(), keyGenerator);
      }

      returnObject = redisCacheService.getFromCache(cacheableAnnotation.cacheName(), cacheKey);

    } catch (Exception e) {
      log.error("Redis op Exception while trying to get from cache: {}", e);
    }
    if (returnObject != null) {
      return returnObject;
    } else {
      returnObject = callActualMethod(proceedingJoinPoint);

      if (returnObject != null) {
        try {
          if (cacheableAnnotation.isAsync()) {
            redisCacheService
                .saveInRedisAsync(new String[] { cacheableAnnotation.cacheName() }, cacheKey,
                    returnObject);
          } else {
            redisCacheService
                .saveInRedis(new String[] { cacheableAnnotation.cacheName() }, cacheKey,
                    returnObject);
          }
        } catch (Exception e) {
          log.error("Exception occurred while trying to save data in redis in get method: {}, {} ",
              e.getMessage(), e);
        }
      }
    }
    return returnObject;
  }

  private Object callActualMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

    return proceedingJoinPoint.proceed();

  }

  private <T extends Annotation> T getAnnotation(JoinPoint proceedingJoinPoint,
      Class<T> annotationClass) throws NoSuchMethodException {

    MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
    Method method = signature.getMethod();
    String methodName = method.getName();
    if (method.getDeclaringClass().isInterface()) {
      method = proceedingJoinPoint.getTarget().getClass().getDeclaredMethod(methodName,
          method.getParameterTypes());
    }
    return method.getAnnotation(annotationClass);
  }

}
