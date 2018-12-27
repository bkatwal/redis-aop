package com.bkatwal.annotation;

import com.bkatwal.util.KeyGenerators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.bkatwal.util.KeyGenerators.SHA;

/**
 * Created by bikashkumarkatwal on 9/3/17.
 */

/**
 * Use this annotation, to get value from cache if exist if not will save the corresponding get
 * method result to cache
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

  String cacheName() default "";

  /**
   * Indicates the time to live for a cache entity in cache storage. Minutes should be used as units
   *
   * @return
   */
  long TTL() default -1L;

  /**
   * key expression parameter is used to indicate the value for cache key. It can be any input
   * argument of method or a field from return object or can be any field from input object type
   * too. Example usage:- 1. args[0] will indicate first argument of method will be used as cache
   * key 2. args[1].account indicates account field of 2nd argument(object) will be used as cache
   * key
   * NOTE: if nothing is passed all keys will be used to generate the unique key
   * @return
   */
  String keyExpression() default "";

  /**
   * should be made true if save operation needs to be async
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