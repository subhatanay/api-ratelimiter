package com.subhadev.ratelimiter.config;

import com.subhadev.ratelimiter.exception.ConfigLoadException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.Map;

public class RuleYAMLConfigLoader implements ConfigLoader {
    private static final String DEFAULT_RULE_CONFIG_FILE = "rate-limit-rule.yaml";
    private Map<String, Object> rateLimitingRules;
    private Long lastConfigUpdated;
    private String currentRuleFilePath;

    public RuleYAMLConfigLoader() {
        this.loadConfig();
    }
    public RuleYAMLConfigLoader(String ruleFile) {
        this.currentRuleFilePath = ruleFile;
        this.loadConfig();
    }

    public RuleYAMLConfigLoader(Map<String, Object> rateLimitingRules) {
        this.rateLimitingRules = rateLimitingRules;
    }
    @Override
    public void loadConfig() throws ConfigLoadException {
        try {
            Yaml yaml = new Yaml();
            File configFile =  getFile();
            Map<String, Object> rulesInfo = yaml.load(new FileInputStream(configFile));
            this.rateLimitingRules = rulesInfo;
            this.lastConfigUpdated = configFile.lastModified();
        } catch (Exception ex) {
            throw new ConfigLoadException(ex.getMessage());
        }
    }

    private File getFile() throws URISyntaxException {
        if (this.currentRuleFilePath == null) {
            return new File(this.getClass().getClassLoader().getResource(DEFAULT_RULE_CONFIG_FILE).toURI());
        } else {
            return new File(this.currentRuleFilePath);
        }
    }


    @Override
    public boolean isConfigChanged() {
        try {
            File configFile =   getFile();
            return this.lastConfigUpdated != configFile.lastModified();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    @Override
    public Map<String, Object> getRulesContent() {
        return this.rateLimitingRules;
    }
}
