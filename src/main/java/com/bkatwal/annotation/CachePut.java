package com.bkatwal.annotation;

/**
 * Created by bikashkumarkatwal on 9/3/17.
 */

import com.bkatwal.util.KeyGenerators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.bkatwal.util.KeyGenerators.SHA;


/**
 * use this annotation on method, to save the return/result of method to cache.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachePut {

  String[] cacheNames() default {};

  /**
   * Indicates the time to live for a cache in cache storage. Hours should be used as units Avoid
   * using this as TTL would apply to whole hash if applied to redis cache, hence invalidating whole
   * cache
   *
   * @return
   */
  long TTL() default Long.MAX_VALUE;

  /**
   * key expression parameter is used to indicate the value for cache key. It can be any input
   * argument of method or a field from return object or can be any field from input object type
   * too. Example usage:-
   * 1. #param1 indicates first argument of method will be used as cache key
   * 2. #param2.account indicates account field of 2nd argument(object) will be used as cache key
   * 3. #result.id indicates id field of method return object will be used as cache key
   *
   * @return
   */
  String keyExpression();

  /**
   * pass true if you want to make save to redis request async
   *
   * @return
   */
  boolean isAsync() default false;


  /**
   * specify key generator, currently 2 types of key generator supported SHA based on params and parms concat
   * @return
   */
  KeyGenerators keyGenerator() default SHA;

  /**
   * specify true if data needs to be compressed
   * @return
   */
  boolean compress() default false;
}
