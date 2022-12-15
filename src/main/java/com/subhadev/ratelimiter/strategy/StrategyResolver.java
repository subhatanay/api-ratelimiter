package com.subhadev.ratelimiter.strategy;

import com.subhadev.ratelimiter.strategy.inmemory.InMemorySlidingWindow;
import com.subhadev.ratelimiter.strategy.inmemory.InMemoryTokenBucket;
import com.subhadev.ratelimiter.strategy.redis.RedisConnectionFactory;
import com.subhadev.ratelimiter.strategy.redis.RedisSlidingWindowLogStrategy;
import com.subhadev.ratelimiter.strategy.redis.RedisTokenBucketLogStrategy;

import java.io.IOException;
import java.net.URISyntaxException;

public class StrategyResolver {

    public static RateLimitingStrategy getRateLimitingStrategy(String strategy,  boolean isMemory) throws URISyntaxException, IOException {
        switch (strategy) {
            case "sliding":
                return isMemory ? new InMemorySlidingWindow() : new RedisSlidingWindowLogStrategy(RedisConnectionFactory.getRedisClient());
            case "token-bucket":
                return isMemory ? new InMemoryTokenBucket() : new RedisTokenBucketLogStrategy(RedisConnectionFactory.getRedisClient());
            default:
                return null;
        }
    }

}
