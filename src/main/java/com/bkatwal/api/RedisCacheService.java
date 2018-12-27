package com.bkatwal.api;

/**
 * Created by bikashkumarkatwal on 12/12/18.
 */
public interface RedisCacheService {

  Object getFromCache(String cacheName, Object cacheKey);

  boolean saveInRedis(String[] cacheNames, Object cacheKey, Object cacheValue);

  boolean invalidateCache(String[] cacheNames, Object cacheKey);

  boolean invalidateCache(String[] cacheNames);

  boolean removeAllCache();

  boolean saveInRedisAsync(String[] cacheNames, Object cacheKey, Object cacheValue);

  boolean invalidateCacheAsync(String[] cacheNames, Object cacheKey);

  boolean invalidateCacheAsync(String[] cacheNames);

  boolean removeAllCacheAsync();
}
