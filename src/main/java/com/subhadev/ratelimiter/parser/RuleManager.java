package com.subhadev.ratelimiter.parser;

import com.subhadev.ratelimiter.models.RateLimitingRulesInfo;
import com.subhadev.ratelimiter.models.RuleParams;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class RuleManager {
    private static Logger logger = LoggerFactory.getLogger(RuleManager.class);
    private RuleConfigParser ruleConfigParser;
    @Getter
    private RateLimitingRulesInfo rateLimitingRulesInfo;

    public RuleManager(RuleConfigParser ruleConfigParser) {
        this.ruleConfigParser = ruleConfigParser;
        this.rateLimitingRulesInfo = this.ruleConfigParser.getRateLimitingRulesInfo();
    }

    public Map<String, RuleParams> getMatchedRules(HttpServletRequest request) {

        this.ruleConfigParser.reloadConfig();
        Map<String, RuleParams> filtersForRateLimiting = new HashMap<>();
        this.ruleConfigParser.getRateLimitingRulesInfo().getRules().forEach(rule -> {
            String filterKey = rule.matchesAndReturnFilterKey(request);
            if (filterKey != null) {
                filtersForRateLimiting.put(filterKey, rule.getRuleProperties());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Matched Filter Keys :: " + filterKey + " with Bucket Info " + rule.getRuleProperties());
            }
        });
        return filtersForRateLimiting;
    }

}
