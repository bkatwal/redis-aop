package com.bkatwal;

import com.bkatwal.api.RedisCacheService;
import com.bkatwal.configuration.RedisConfiguration;
import com.bkatwal.util.CacheUtil;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.embedded.RedisServer;

import java.io.IOException;

/**
 * created by bikas katwal on 26/12/18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RedisConfiguration.class })
public class RedisCacheServiceTest {

  @Autowired RedisCacheService redisCacheService;

  private static RedisServer redisServer;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  private HashOperations<String, Object, Object> hashOperations;

  @Autowired
  private TestService testService;

  @BeforeClass
  public static void init() throws IOException {
    redisServer = new RedisServer(6379);
    redisServer.start();
  }

  @AfterClass
  public static void destroy() {
    // do some work
    redisServer.stop();
  }

  @Test
  public void dummyTest() {

  }

  @Before
  public void initHash() {
    hashOperations = redisTemplate.opsForHash();
  }

  @Test
  public void redisOpTest() {

    String[] cacheNames = new String[1];
    cacheNames[0] = "CACHE";
    redisCacheService.saveInRedis(cacheNames, "key1", "value");

    String val = (String) redisCacheService.getFromCache(cacheNames[0], "key1");
    Assert.assertEquals("value", val);

    redisCacheService.invalidateCache(cacheNames, "key1");

    Object object = redisCacheService.getFromCache(cacheNames[0], "key1");
    Assert.assertNull(object);

    redisCacheService.saveInRedis(cacheNames, "key2", "value");

    redisCacheService.invalidateCache(cacheNames);

    object = redisCacheService.getFromCache(cacheNames[0], "key2");
    Assert.assertNull(object);

    redisCacheService.removeAllCache();
  }

  @Test
  public void annotationCachePutTest() {
    testService.saveByIdAndName(1, "testName");

    TestPojo testPojo = (TestPojo) redisCacheService
        .getFromCache("PUT_CACHE1", CacheUtil.buildCacheKey(new Object[] { 1, "testName" }));

    Assert.assertEquals(new TestPojo(1, "testName"), testPojo);

    testService.saveByIdAndName2(2, "testName2");

    testPojo = (TestPojo) redisCacheService
        .getFromCache("PUT_CACHE2", CacheUtil.buildStringCacheKey(new Object[] { 2, "testName2" }));

    Assert.assertEquals(new TestPojo(2, "testName2"), testPojo);

    testService.saveByObject(new TestPojo(3, "testName3"));

    testPojo = (TestPojo) redisCacheService
        .getFromCache("PUT_CACHE3", CacheUtil.buildCacheKey(new Object[] { 3, "testName3" }));

    Assert.assertEquals(new TestPojo(3, "testName3"), testPojo);

    redisCacheService.removeAllCache();

  }

  @Test
  public void testInvalidateCache() {
    String[] cacheNames = new String[1];
    cacheNames[0] = "DELETE_CACHE";

    redisCacheService
        .saveInRedis(cacheNames, CacheUtil.buildCacheKey(new Object[] { 4, "testName4" }),
            new TestPojo(4, "testName4"));

    TestPojo testPojo = (TestPojo) redisCacheService
        .getFromCache("DELETE_CACHE", CacheUtil.buildCacheKey(new Object[] { 4, "testName4" }));
    Assert.assertNotNull(testPojo);
    testService.deleteObject(4, "testName4");

    testPojo = (TestPojo) redisCacheService
        .getFromCache("DELETE_CACHE", CacheUtil.buildCacheKey(new Object[] { 4, "testName4" }));
    Assert.assertNull(testPojo);

    redisCacheService.removeAllCache();
  }

  @Test
  public void testGetAndSave() {

    TestPojo testPojo = (TestPojo) redisCacheService
        .getFromCache("GET_CACHE", CacheUtil.buildCacheKey(new Object[] { 5 }));
    Assert.assertNull(testPojo);

    testPojo = testService.getObjectById("testName5", 5);

    TestPojo testPojo2 = (TestPojo) redisCacheService
        .getFromCache("GET_CACHE", CacheUtil.buildCacheKey(new Object[] { 5 }));
    Assert.assertEquals(testPojo, testPojo2);

    redisCacheService.removeAllCache();
  }
}
