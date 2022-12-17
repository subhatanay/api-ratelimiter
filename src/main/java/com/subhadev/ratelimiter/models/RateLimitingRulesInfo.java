package com.subhadev.ratelimiter.models;

import com.subhadev.ratelimiter.ruletypes.RuleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RateLimitingRulesInfo {
    private List<RuleType> rules;
    private String strategy;
    private boolean isMemory;
    private String redisIp;
    private String redisPort;
}
