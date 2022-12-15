package com.subhadev.ratelimiter.strategy;

public abstract class TokenBucketStrategy implements RateLimitingStrategy {
    public abstract void createToken(String key, Object value);

}
