package com.subhadev.ratelimiter.strategy;

import com.subhadev.ratelimiter.models.RateLimitingRulesInfo;
import com.subhadev.ratelimiter.strategy.inmemory.InMemorySlidingWindow;
import com.subhadev.ratelimiter.strategy.inmemory.InMemoryTokenBucket;
import com.subhadev.ratelimiter.strategy.redis.RedisConnectionFactory;
import com.subhadev.ratelimiter.strategy.redis.RedisSlidingWindowLogStrategy;
import com.subhadev.ratelimiter.strategy.redis.RedisTokenBucketLogStrategy;

import java.io.IOException;
import java.net.URISyntaxException;

public class StrategyResolver {

    public static RateLimitingStrategy getRateLimitingStrategy(RateLimitingRulesInfo config) throws URISyntaxException, IOException {
        switch (config.getStrategy()) {
            case "sliding":
                return config.isMemory() ? new InMemorySlidingWindow() : new RedisSlidingWindowLogStrategy(RedisConnectionFactory.getRedisClient(config.getRedisIp(),config.getRedisPort()));
            case "token-bucket":
                return config.isMemory() ? new InMemoryTokenBucket() : new RedisTokenBucketLogStrategy(RedisConnectionFactory.getRedisClient(config.getRedisIp(),config.getRedisPort()));
            default:
                return null;
        }
    }

}
