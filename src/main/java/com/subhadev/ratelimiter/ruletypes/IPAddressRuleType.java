package com.subhadev.ratelimiter.ruletypes;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class IPAddressRuleType extends RuleType {
    private String ipAddressToMatch;
    @Override
    public String extractRequestFilterKey() {
        return ipAddressToMatch;
    }
    @Override
    public String getRequestFilterKeyPrefix() {
        return  "IPAddress:" + ipAddressToMatch;
    }
    public static IpAddressRuleTypeBuilder builder() {
        return new IpAddressRuleTypeBuilder();
    }

    @Override
    public String matchesAndReturnFilterKey(HttpServletRequest httpServletRequest) {
        if (this.ipAddressToMatch.equals("*")) {
            return getRequestFilterKeyPrefix();
        }
        String remoteAddress = httpServletRequest.getRemoteAddr();
        return extractRequestFilterKey().contains(remoteAddress) ? getRequestFilterKeyPrefix() : null;
    }

    private static class IpAddressRuleTypeBuilder extends RuleTypeBuilder {
        private String ipaddress;
        public IPAddressRuleType build() {
            IPAddressRuleType ipAddressRuleType = new IPAddressRuleType();
            ipAddressRuleType.ruleName = this.rName;
            ipAddressRuleType.ipAddressToMatch = this.ipaddress;
            ipAddressRuleType.ruleProperties = this.ruleParams;

            return ipAddressRuleType;
        }
        @Override
        public RuleTypeBuilder values(List<String> values) {
            this.ipaddress = values.get(0);
            return this;
        }
    }
 }
