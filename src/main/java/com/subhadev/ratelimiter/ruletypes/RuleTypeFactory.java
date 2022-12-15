package com.subhadev.ratelimiter.ruletypes;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class RuleTypeFactory {

    public static boolean isMultiTypeRule(String type) {
        return type.contains(",");
    }

    public static RuleType.RuleTypeBuilder getRuleType(String type) {
        switch(type) {
            case "ip-address":
                return  IPAddressRuleType.builder();
            case "request-header":
                return RequestHeaderRuleType.builder();
            case "request-url-regex":
                return RequestURIRegexRuleType.builder();
            default:
                return null;
        }
    }

    public static ConfigKeyInfo[] extractConfigKeys(String type) {
        switch(type) {
            case "ip-address":
                return  new ConfigKeyInfo[] { new ConfigKeyInfo("ipaddress",false) };
            case "request-header":
                return new ConfigKeyInfo[] { new ConfigKeyInfo("header",false) };
            case "request-url-regex":
                return new ConfigKeyInfo[] { new ConfigKeyInfo("urlRegex",false),new ConfigKeyInfo("pathParamIndex",true) } ;
            default:
                return null;
        }
    }
    @Getter
    @AllArgsConstructor
    public static class ConfigKeyInfo {
        private String keyName;
        private boolean optional;
    }

}
