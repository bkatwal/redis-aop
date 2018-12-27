package com.bkatwal.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.Getter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * created by bikas katwal on 25/12/18
 */
@Getter
public class KryoRedisSerializer implements RedisSerializer<Object> {

  private Kryo kryo;

  public KryoRedisSerializer(List<Class<?>> classes) {
    kryo = new Kryo();
    for (Class<?> clazz : classes) {
      kryo.register(clazz);
    }
  }

  public KryoRedisSerializer() {
    kryo = new Kryo();
  }

  @Override
  public byte[] serialize(Object object) throws SerializationException {
    ByteArrayOutputStream objStream = new ByteArrayOutputStream();
    Output objOutput = new Output(objStream);
    kryo.writeClassAndObject(objOutput, object);
    objOutput.close();
    return objStream.toByteArray();
  }

  @Override
  public Object deserialize(byte[] bytes) throws SerializationException {
    return kryo.readClassAndObject(new Input(bytes));
  }

}
