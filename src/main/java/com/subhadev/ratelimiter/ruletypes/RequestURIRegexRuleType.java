package com.subhadev.ratelimiter.ruletypes;


import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestURIRegexRuleType extends RuleType {
    private String uriRegexToMatch;
    private String pathParamIndexes;
    @Override
    public String matchesAndReturnFilterKey(HttpServletRequest httpServletRequest) {
        String requestUrl = httpServletRequest.getRequestURI();
        Pattern pattern = Pattern.compile(this.uriRegexToMatch);
        Matcher matcher = pattern.matcher(requestUrl);
        String pathParamsGroup = "";
        if (pathParamIndexes!=null) {
            String[] pathParamIndexArr = pathParamIndexes.split(",");
            for (String pathParamIndex : pathParamIndexArr) {
                if (matcher.find()) {
                    pathParamsGroup += ":" + matcher.group(Integer.valueOf(pathParamIndex));
                }
            }
        }
        return matcher.matches() ? getRequestFilterKeyPrefix() +  pathParamsGroup : null;
    }
    @Override
    public String extractRequestFilterKey() {
        return uriRegexToMatch;
    }
    @Override
    public String getRequestFilterKeyPrefix() {
        return "URI:" + uriRegexToMatch;
    }
    public static RequestURIRegexRuleTypeBuilder builder() {
        return new RequestURIRegexRuleTypeBuilder();
    }

    private  static class RequestURIRegexRuleTypeBuilder extends RuleTypeBuilder {
        private String pathparamindex;
        private String uriRegexToMatch;
        public RequestURIRegexRuleType build() {
            RequestURIRegexRuleType requestURIRegexRuleType = new RequestURIRegexRuleType();
            requestURIRegexRuleType.ruleName = this.rName;
            requestURIRegexRuleType.uriRegexToMatch = this.uriRegexToMatch;
            requestURIRegexRuleType.pathParamIndexes = this.pathparamindex;
            requestURIRegexRuleType.ruleProperties = this.ruleParams;

            return requestURIRegexRuleType;
        }
        @Override
        public RuleTypeBuilder values(List<String> values) {
            this.uriRegexToMatch = values.get(0);
            if (values.size() > 1)
                this.pathparamindex = values.get(1);
            return this;
        }
    }
}
