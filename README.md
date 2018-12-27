# redis-aop
redis-aop is a Spring AOP based caching utility for redis, built on top of jedis client. User can make use of several annotation that comes with this utility project.


## Usage
``@CachePut @CacheDelete @Cacheable``, are the three annotaiton that available. 

``@CachePut``: Use it in any save method, when the saved object needs to be saved in redis too. Note, that the save object needs to be returned by method for it to work.

``@CacheDelete``: Use it in any delete method, it will use the key from method params based on input and delete respective cache from redis.

``@CacheDelete``: Use this for any sort of get operation. It will return if the object is available in redis based on input param key, if not available, will call the get method and save in redis then return the object to client.

#### Annotation Params:
|Param Name|Description|Default|
|----------|-----------|--------|
|cacheName|Cache name from where data needs to be fetched. This is redis key| |
|keyExpression|This expression will determine the cache key from method input params| |
|isAsync| if ``true`` save in redis operation will be async|false|
|keyGenerator|two values supported in this ``SHA`` or ``CONCAT``. If ``SHA``, cache key will be created using sha hash of param combination. If ``CONCAT`` is used, cache key will be created by concatenating params.| SHA|
|compress|If set to ``true``, framework will use GZIP compression on data.|false

## How it works?



## Getting started: project setup
