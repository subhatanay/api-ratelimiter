package com.subhadev.ratelimiter.ruletypes;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class RequestHeaderRuleType  extends RuleType {
    private String headerKey;
    @Override
    public String extractRequestFilterKey() {
        return headerKey;
    }

    @Override
    public String getRequestFilterKeyPrefix() {
        return "HeaderKey:" + headerKey;
    }
    public static RequestHeaderRuleTypeBuilder builder() {
        return new RequestHeaderRuleTypeBuilder();
    }

    @Override
    public String matchesAndReturnFilterKey(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(extractRequestFilterKey()) !=null ? getRequestFilterKeyPrefix() + ":" + httpServletRequest.getHeader(extractRequestFilterKey()) : null;
    }

    private  static class RequestHeaderRuleTypeBuilder extends RuleTypeBuilder {
        private String headerKey;
        public RequestHeaderRuleType build() {
            RequestHeaderRuleType requestHeaderRuleType = new RequestHeaderRuleType();
            requestHeaderRuleType.ruleName = this.rName;
            requestHeaderRuleType.headerKey = this.headerKey;
            requestHeaderRuleType.ruleProperties = this.ruleParams;

            return requestHeaderRuleType;
        }
        @Override
        public RuleTypeBuilder values(List<String> values) {
            this.headerKey = values.get(0);
            return this;
        }
    }
}
