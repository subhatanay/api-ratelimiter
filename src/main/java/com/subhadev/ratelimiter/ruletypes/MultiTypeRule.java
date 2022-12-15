package com.subhadev.ratelimiter.ruletypes;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class MultiTypeRule extends RuleType {
    private List<RuleType> ruleTypes;

    public MultiTypeRule(List<RuleType> ruleTypes) {
        this.ruleTypes = ruleTypes;
        this.ruleProperties = ruleTypes.get(0).getRuleProperties();
    }

    @Override
    public String matchesAndReturnFilterKey(HttpServletRequest httpServletRequest) {
        String matchedRuleKey = "";
        for (RuleType rule : ruleTypes) {
            String matchKey = rule.matchesAndReturnFilterKey(httpServletRequest);
            if (matchKey == null) {
                return null;
            }
            matchedRuleKey += "*"+ matchKey;
        }
        return matchedRuleKey;
    }

    @Override
    public String extractRequestFilterKey() {
        return null;
    }

    @Override
    public String getRequestFilterKeyPrefix() {
        return "";
    }
}
