package com.subhadev.ratelimiter.strategy;

public abstract class SlidingWindowStrategy implements RateLimitingStrategy {
    public abstract void createToken(String key);

}
