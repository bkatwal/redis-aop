package com.bkatwal.annotation;

/**
 * Created by bikashkumarkatwal on 9/3/17.
 */

import com.bkatwal.util.KeyGenerators;

import java.lang.annotation.*;

import static com.bkatwal.util.KeyGenerators.SHA;

/**
 * Use this to remove one or more cache from cache storage
 */

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CacheDelete {

  /**
   * cacheNames accepts one or more cacheNames as input.
   *
   * @return
   */
  String[] cacheNames() default {};

  /**
   * pass this flag as true if all entries of give cache names needs to be removed
   *
   * @return
   */
  boolean removeAll() default false;

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
  String keyExpression() default "";

  /**
   * pass true if you want to make delete to redis request async
   *
   * @return
   */
  boolean isAsync() default false;

  /**
   * specify key generator, currently 2 types of key generator supported SHA based on params and parms concat
   *
   * @return
   */
  KeyGenerators keyGenerator() default SHA;

  /**
   * specify true if data needs to be compressed
   *
   * @return
   */

}
