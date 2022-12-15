package com.subhadev.ratelimiter.ruletypes;


import com.subhadev.ratelimiter.models.RuleParams;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class RuleType implements RuleMatcher {
    protected String ruleName;
    protected RuleParams ruleProperties;

    public abstract String extractRequestFilterKey();

    public abstract String getRequestFilterKeyPrefix();

    public static  abstract class RuleTypeBuilder {
        protected String rName;
        protected RuleParams ruleParams;

        public RuleTypeBuilder name(String rName) {
            this.rName = rName;
            return this;
        }
        public RuleTypeBuilder ruleProperties(RuleParams params) {
            this.ruleParams = params;
            return this;
        }
        public abstract RuleTypeBuilder values(List<String> values);

        public abstract RuleType build();
    }

}
