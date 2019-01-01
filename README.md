### GitLab [![build status](https://gitlab.com/bikas.katwal10/redis-aop/badges/master/build.svg)](https://gitlab.com/bikas.katwal10/redis-aop/master)

# redis-aop
redis-aop is a Spring AOP based caching utility for redis, built on top of jedis client. User can make use of several annotation that comes with this utility project.


## Usage
``@CachePut @CacheDelete @Cacheable``, are the three annotaiton that are available. 

``@CachePut``: Use it in any save method, when the saved object needs to be saved in redis too. Note, that the save object needs to be returned by method for it to work.

``@CacheDelete``: Use it in any delete method, it will use the key from method params based on input and delete respective cache from redis.

``@CacheDelete``: Use this for any sort of get operation. It will return if the object is available in redis based on input param key, if not available, will call the get method and save in redis then return the object to client.

#### Annotation Params:
|Param Name|Description|Default|
|----------|-----------|--------|
|cacheName or cacheNames(Used in put and save)|Cache name from where data needs to be fetched. This is redis key| |
|keyExpression|This expression will determine the cache key from method input params| |
|isAsync| if ``true`` save in redis operation will be async|false|
|keyGenerator|two values supported in this ``SHA`` or ``CONCAT``. If ``SHA``, cache key will be created using sha hash of param combination. If ``CONCAT`` is used, cache key will be created by concatenating params.| SHA|
|compress|If set to ``true``, framework will use GZIP compression on data.|false

### Examples:
1. Below Example depicts save operation:
~~~
@CachePut(cacheNames = {
      "CUSTOM_OBJECT_CACHE" }, keyExpression = "#param1.id,#param1.name")
public CustomObject saveObject(CustomObject object){
  return customRepository.save(object);
}
~~~
In above ``#param1`` represents first param,``#param2`` represents second and so on.

2. Below example depicts delete operation.
~~~
@CacheDelte(cacheNames = {
      "CUSTOM_OBJECT_CACHE" }, keyExpression = "#param1")
public void deleteById(int id){
  //some delete operation
}
~~~
Above will delete any data in key CUSTOM_OBJECT_CACHE for hash ``#param1`` i.e. given ``id``

3. Below example depicts get operation:
~~~
@Cacheable(cacheName = "CUSTOM_OBJECT_CACHE", keyExpression = "#param1,#param2")
public void getByIdAndName(int id, String name){
  //some  operation
  return customObject;
}
~~~

#### NOTE: If no ``keyExpression`` is passed, then all params will be used for creating cache key.
## How it works?
Uses spring AOP for annotation based interceptors and to parse input params Spring Expression is used.


## Getting started: project setup
Just edit the [redis.properties](
        redis-aop/src/main/resources/redis.properties
      ) file
