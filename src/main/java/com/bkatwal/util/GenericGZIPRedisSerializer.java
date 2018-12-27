package com.bkatwal.util;

import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by bikashkumarkatwal on 10/3/17.
 */
public class GenericGZIPRedisSerializer
    implements RedisSerializer<Object> {

  public byte[] serialize(Object object) {

    ByteArrayOutputStream baos;
    GZIPOutputStream gzipOut;
    ObjectOutputStream objectOutputStream;
    try {
      baos = new ByteArrayOutputStream();
      gzipOut = new GZIPOutputStream(baos);
      objectOutputStream = new ObjectOutputStream(gzipOut);
      objectOutputStream.writeObject(object);
      objectOutputStream.flush();
      gzipOut.finish();
      objectOutputStream.close();
      return baos.toByteArray();
    } catch (Exception e) {
      throw new RedisSystemException(
          "Could not serialize. ", e);
    }
  }

  public Object deserialize(byte[] bytes) {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      GZIPInputStream gin = new GZIPInputStream(bais);
      ObjectInputStream ois = new ObjectInputStream(gin);
      return ois.readObject();
    } catch (Exception e) {
      if (e.getMessage().contains("InvalidClassException")) {
        throw new RedisSystemException(
            "Could not deserialize. Invalid version of class found: ", e);
      } else {
        throw new RedisSystemException("Could not deserialize. ", e);
      }
    }
  }
}
