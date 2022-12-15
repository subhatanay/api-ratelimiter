package com.subhadev.ratelimiter.parser;

import com.subhadev.ratelimiter.models.RuleParams;
import com.subhadev.ratelimiter.config.ConfigLoader;
import com.subhadev.ratelimiter.models.RateLimitingRulesInfo;
import com.subhadev.ratelimiter.ruletypes.MultiTypeRule;
import com.subhadev.ratelimiter.ruletypes.RuleType;
import com.subhadev.ratelimiter.ruletypes.RuleTypeFactory;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class RuleConfigParser {
    private static Logger logger = LoggerFactory.getLogger(RuleConfigParser.class);
    private final ConfigLoader configLoader;
    private RateLimitingRulesInfo rateLimitingRulesInfo;

    public RuleConfigParser(ConfigLoader configLoader) {
        this.configLoader = configLoader;
        this.rateLimitingRulesInfo =  this.parseConfig();
    }

    public synchronized RateLimitingRulesInfo parseConfig() {
        Map<String,Object> rulesConfig = this.configLoader.getRulesContent();

        String rateLimitStrategy = (String) rulesConfig.get("strategy");
        Boolean inMemory = (Boolean) rulesConfig.get("in-memory");
        List<Map<String,String>> rateLimitingRules = (List<Map<String, String>>) rulesConfig.get("rules");

        logger.debug("Loaded strategy {0} , im-memory {1}", rateLimitStrategy, inMemory);

        List<RuleType> ruleTypes = rateLimitingRules.stream().map(rule -> {
            String type = rule.get("type");
            boolean isMultiTypeRule = RuleTypeFactory.isMultiTypeRule(type);
            logger.debug("Loading rule with name : {0} in RuleManager", rule.get("name"));
            if (isMultiTypeRule) {
                return getMultiTypeRule(rule, type, rateLimitStrategy);
            } else {
                return getRuleType(rule, type, rateLimitStrategy);
            }
        }).filter(rule -> rule!=null).collect(Collectors.toList());

        return  RateLimitingRulesInfo
                .builder()
                .rules(ruleTypes)
                .isMemory(inMemory)
                .strategy(rateLimitStrategy)
                .build();
    }

    private RuleType getRuleType(Map<String, String> ruleContent, String type,String rateLimitStrategy) {
        RuleParams ruleParams = new RuleParams(rateLimitStrategy, ruleContent);
        RuleType.RuleTypeBuilder ruleTypeFactory = RuleTypeFactory.getRuleType(type);
        if (ruleTypeFactory != null) {
            RuleTypeFactory.ConfigKeyInfo[] keys = RuleTypeFactory.extractConfigKeys(type);

            List<String> values = Arrays.asList(keys)
                    .stream().map(key -> {
                        if (!key.isOptional() && ruleContent.get(key.getKeyName()) == null) {
                            throw new IllegalArgumentException(key.getKeyName() + " should be present in rule type " + type + " rule config file");
                        }
                        return ruleContent.get(key.getKeyName());
                    })
                    .filter(key -> key != null)
                    .collect(Collectors.toList());
            return ruleTypeFactory
                    .name(ruleContent.get("name"))
                    .values(values)
                    .ruleProperties(ruleParams)
                    .build();
        }
        return null;
    }
    private RuleType getMultiTypeRule(Map<String, String> ruleContent, String type,String rateLimitStrategy) {
        String[] typeList = type.split(",");
        List<RuleType> rulesTypes = Arrays.asList(typeList).stream().map(t -> getRuleType(ruleContent,t,rateLimitStrategy)).filter(rule-> rule!=null).collect(Collectors.toList());
        return new MultiTypeRule(rulesTypes);
    }

    public void reloadConfig() {
        try {
            if (this.configLoader.isConfigChanged()) {
                logger.info("Rule config got changed. Reloading the config.");
                this.configLoader.loadConfig();
                this.rateLimitingRulesInfo =  this.parseConfig();
                logger.info("Reloading the config successfully");
            }
        } catch (Exception ex) {
            logger.error("Error while reloading rule config. Please verify the config. Reason : " + ex.getMessage());
        }
    }

}
