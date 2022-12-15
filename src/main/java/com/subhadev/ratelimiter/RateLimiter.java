package com.subhadev.ratelimiter;

import com.subhadev.ratelimiter.models.RuleParams;
import com.subhadev.ratelimiter.parser.RuleManager;
import com.subhadev.ratelimiter.strategy.RateLimitingStrategy;
import com.subhadev.ratelimiter.strategy.StrategyResolver;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@Getter
public class RateLimiter {
    private static Logger logger = LoggerFactory.getLogger(RateLimiter.class);
    private RuleManager ruleManager;
    private RateLimitingStrategy rateLimitingStrategy;
    public RateLimiter(RuleManager ruleManager) throws URISyntaxException, IOException {
        this.ruleManager = ruleManager;
        this.rateLimitingStrategy = StrategyResolver.getRateLimitingStrategy(this.getRuleManager().getRateLimitingRulesInfo().getStrategy(),this.getRuleManager().getRateLimitingRulesInfo().isMemory());
        logger.info("Rate limiter initialized successfully");
    }

    public RateLimiter(RuleManager ruleManager,RateLimitingStrategy rateLimitingStrategy) {
        this.ruleManager = ruleManager;
        this.rateLimitingStrategy = rateLimitingStrategy;
        System.out.println(rateLimitingStrategy.getClass().getName());
    }
    public boolean isRequestAllowed(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("http request should not be null");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Requested URL :: " + request.getRequestURI());
        }
        Map<String, RuleParams> filteredKeys = this.ruleManager.getMatchedRules(request);
        boolean isAllowed =true;
        for (Map.Entry<String, RuleParams> filter : filteredKeys.entrySet()) {
            boolean allow = this.rateLimitingStrategy.isRequestAllowed(filter.getKey(), filter.getValue());
            if (logger.isDebugEnabled()) {
                logger.debug("Matched Filter Key :: " +filter.getKey() + " Requested Allowed :: " + allow);
            }
            isAllowed = isAllowed && allow;
        }
        if (!isAllowed) {
            logger.info("Requested url :: " + request.getRequestURI() + " got rate limited.");
        }
        return isAllowed;
    }
}
