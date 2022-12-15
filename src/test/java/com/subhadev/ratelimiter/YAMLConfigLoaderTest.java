package com.subhadev.ratelimiter;

import com.subhadev.ratelimiter.config.ConfigLoader;
import com.subhadev.ratelimiter.config.RuleYAMLConfigLoader;
import com.subhadev.ratelimiter.exception.ConfigLoadException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class YAMLConfigLoaderTest {

    @Test
    public void testLoadYAMLConfigSuccessWithDefaultFile() {
        ConfigLoader configLoader = new RuleYAMLConfigLoader();
        Map<String, Object> ruleContents =  configLoader.getRulesContent();

        Assertions.assertNotNull(ruleContents);
        Assertions.assertEquals("sliding", ruleContents.get("strategy"));
        Assertions.assertEquals(true, ruleContents.get("in-memory"));
        Assertions.assertNotNull(ruleContents.get("rules"));
    }

//    @Test
//    public void testLoadYAMLConfigSuccessWithPathFile() {
//        ConfigLoader configLoader = new RuleYAMLConfigLoader("./../../../resources/rate-limit-rule.yaml");
//        Map<String, Object> ruleContents =  configLoader.getRulesContent();
//
//        Assertions.assertNotNull(ruleContents);
//        Assertions.assertEquals("sliding", ruleContents.get("strategy"));
//        Assertions.assertEquals(true, ruleContents.get("in-memory"));
//        Assertions.assertNotNull(ruleContents.get("rules"));
//    }

    @Test
    public void testLoadYAMLConfigErrorFile() {
        Assertions.assertThrows(ConfigLoadException.class, () -> {
            ConfigLoader configLoader = new RuleYAMLConfigLoader("test.yml");
        });
    }

}
