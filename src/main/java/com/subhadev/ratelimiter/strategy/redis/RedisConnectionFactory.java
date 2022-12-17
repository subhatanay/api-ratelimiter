package com.subhadev.ratelimiter.strategy.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class RedisConnectionFactory {

    private static Logger logger = LoggerFactory.getLogger(RedisConnectionFactory.class);

    private static Jedis jedis;

    public static Jedis getRedisClient(String ip, String port) {
        if (jedis == null) {
            synchronized (RedisConnectionFactory.class) {
                if (jedis == null) {
                    JedisPool jedisPool = new JedisPool(buildPoolConfig(),ip, Integer.parseInt(port));
                    jedis = jedisPool.getResource();
                    logger.info("Successfully connected to Redis cluster");
                }
            }
        }
       return jedis;
    }

    private static JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

}
