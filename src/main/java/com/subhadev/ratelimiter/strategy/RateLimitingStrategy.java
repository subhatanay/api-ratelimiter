package com.subhadev.ratelimiter.strategy;

import com.subhadev.ratelimiter.models.RuleParams;

public interface RateLimitingStrategy {
    public boolean isRequestAllowed(String request, RuleParams params);
}
