package com.bkatwal;

import com.bkatwal.annotation.CacheDelete;
import com.bkatwal.annotation.CachePut;
import com.bkatwal.annotation.Cacheable;
import com.bkatwal.util.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * created by bikas katwal on 28/12/18
 */

@Component
public class TestService implements Serializable {

  @CachePut(cacheNames = {
      "PUT_CACHE1" }, keyExpression = "#param1,#param2")
  public TestPojo saveByIdAndName(int id, String name) {

    return new TestPojo(id, name);
  }

  @CachePut(cacheNames = {
      "PUT_CACHE2" }, keyExpression = "#param1,#param2", keyGenerator = KeyGenerators.CONCAT)
  public TestPojo saveByIdAndName2(int id, String name) {

    return new TestPojo(id, name);
  }

  @CachePut(cacheNames = {
      "PUT_CACHE3" }, keyExpression = "#param1.id,#param1.name", keyGenerator = KeyGenerators.SHA)
  public TestPojo saveByObject(TestPojo testPojo) {

    return new TestPojo(testPojo);
  }

  @CacheDelete(cacheNames = {
      "DELETE_CACHE" }, keyExpression = "#param1,#param2")
  public boolean deleteObject(int id, String name) {
    //some delete operation
    return true;
  }

  @Cacheable(cacheName = "GET_CACHE", keyExpression = "#param2")
  public TestPojo getObjectById(String name, int id) {
    return new TestPojo(id, name);
  }
}

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode class TestPojo {
  private static final long serialVersionUID = 6414799007524074403L;
  private int id;
  private String name;

  public TestPojo(TestPojo testPojo) {
    id = testPojo.id;
    name = testPojo.name;
  }
}
