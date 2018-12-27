package com.bkatwal.configuration;

import com.bkatwal.util.GenericGZIPRedisSerializer;
import com.bkatwal.util.KryoRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableAsync;
import redis.clients.jedis.JedisPoolConfig;

/**
 * created by bikas katwal on 25/12/17
 */
@Configuration
@EnableAsync
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.bkatwal")
@PropertySource(value = "classpath:redis.properties")
public class RedisConfiguration {

  @Bean
  public StringRedisSerializer stringRedisSerializer() {
    return new StringRedisSerializer();
  }

  @Bean
  public JedisPoolConfig jedisPoolConfig(@Value("${redis.pool.maxTotal}") int maxActive,
      @Value("${redis.pool.maxIdle}")
          int maxIdle, @Value("${redis.pool.minIdle}") int minIdle) {
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxTotal(maxActive);
    jedisPoolConfig.setMaxIdle(maxIdle);
    jedisPoolConfig.setMinIdle(minIdle);
    return jedisPoolConfig;
  }

  @Bean
  public JedisConnectionFactory connectionFactory(JedisPoolConfig jedisPoolConfig,
      @Value("${redis.host}") String host,
      @Value("${redis.port}") int port, @Value("${redis.timeout}") int timeout) {

    JedisConnectionFactory connectionFactory = new JedisConnectionFactory(jedisPoolConfig);

    connectionFactory.setHostName(host);
    connectionFactory.setPort(port);
    connectionFactory.setTimeout(timeout);
    connectionFactory.setUsePool(true);
    return connectionFactory;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      @Value("${redis.useCompression}") boolean useCompression,
      JedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setExposeConnection(true);
    redisTemplate.setKeySerializer(stringRedisSerializer());

    if (useCompression) {
      redisTemplate.setHashValueSerializer(new GenericGZIPRedisSerializer());
    } else {
      redisTemplate.setHashValueSerializer(new KryoRedisSerializer());
    }

    redisTemplate.setEnableTransactionSupport(true);
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }

}
