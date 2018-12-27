package com.bkatwal.impl;

import com.bkatwal.api.RedisCacheService;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * created by bikas katwal on 26/12/18
 */
@Service
@Slf4j
public class RedisCacheServiceImpl implements RedisCacheService {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  private HashOperations<String, Object, Object> hashOperations;

  @PostConstruct
  public void init(){
    hashOperations = redisTemplate.opsForHash();
  }

  @Override public Object getFromCache(final String cacheName, final Object cacheKey) {
    if (Strings.isNullOrEmpty(cacheName) || cacheKey == null) {
      throw new IllegalArgumentException("Cache name or cache key can not be null!");
    }
    return hashOperations.get(cacheName, cacheKey);
  }

  @Override public boolean saveInRedis(final String[] cacheNames, final Object cacheKey,
      final Object cacheValue) {

    if (cacheNames == null || cacheNames.length == 0) {
      throw new IllegalArgumentException(
          "Cache names list can not be null or empty for save operation!!");
    }

    for (String cacheName : cacheNames) {
      hashOperations.put(cacheName, cacheKey, cacheValue);
    }
    return true;
  }

  @Override public boolean invalidateCache(final String[] cacheNames, final Object cacheKey) {

    if (cacheNames == null || cacheNames.length == 0) {
      throw new IllegalArgumentException(
          "Cache names list can not be null or empty for save operation!!");
    }
    for (String cacheName : cacheNames) {
      if (Strings.isNullOrEmpty(cacheName)) {
        continue;
      }
      if (!redisTemplate.hasKey(cacheName)) {
        continue;
      }
      hashOperations.delete(cacheName, cacheKey);

    }
    return true;
  }

  @Override public boolean invalidateCache(final String[] cacheNames) {
    for (String cacheName : cacheNames) {
      redisTemplate.delete(cacheName);
    }
    return true;
  }

  @Override public boolean removeAllCache() {
    redisTemplate.getRequiredConnectionFactory().getConnection().serverCommands().flushAll();
    return false;
  }

  @Async
  @Override public boolean saveInRedisAsync(final String[] cacheNames, final Object cacheKey,
      final Object cacheValue) {

    if (cacheNames == null || cacheNames.length == 0) {
      throw new IllegalArgumentException(
          "Cache names list can not be null or empty for save operation!!");
    }

    for (String cacheName : cacheNames) {
      hashOperations.put(cacheName, cacheKey, cacheValue);
    }
    return true;
  }

  @Async
  @Override public boolean invalidateCacheAsync(final String[] cacheNames, final Object cacheKey) {

    if (cacheNames == null || cacheNames.length == 0) {
      throw new IllegalArgumentException(
          "Cache names list can not be null or empty for save operation!!");
    }
    for (String cacheName : cacheNames) {
      if (Strings.isNullOrEmpty(cacheName)) {
        continue;
      }
      if (!redisTemplate.hasKey(cacheName)) {
        continue;
      }
      hashOperations.delete(cacheName, cacheKey);

    }
    return true;
  }

  @Async
  @Override public boolean invalidateCacheAsync(final String[] cacheNames) {
    for (String cacheName : cacheNames) {
      redisTemplate.delete(cacheName);
    }
    return true;
  }

  @Async
  @Override public boolean removeAllCacheAsync() {
    redisTemplate.getRequiredConnectionFactory().getConnection().serverCommands().flushAll();
    return false;
  }
}
