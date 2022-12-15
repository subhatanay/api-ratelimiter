package com.subhadev.ratelimiter.config;

import com.subhadev.ratelimiter.exception.ConfigLoadException;

import java.util.Map;

public interface ConfigLoader {
    
    void loadConfig() throws ConfigLoadException;

    boolean isConfigChanged();

    Map<String, Object> getRulesContent();

}
