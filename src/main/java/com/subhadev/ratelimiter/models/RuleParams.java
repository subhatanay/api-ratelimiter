package com.subhadev.ratelimiter.models;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RuleParams {
    private int bucketSize;
    private int refillRate;

    private int slidingWindowPeriod;
    private int slidingWindowMaxRequest;

    public RuleParams(String strategy , Map<String, String> params) {
        switch (strategy) {
            case "token-bucket":
                this.bucketSize = Integer.valueOf(params.get("bucket-size"));
                this.refillRate = Integer.valueOf(params.get("refill-rate"));
                break;
            case "sliding":
                this.slidingWindowPeriod = Integer.valueOf((String)params.get("windowPeriodInSecs"));
                this.slidingWindowMaxRequest = Integer.valueOf((String)params.get("maxRequests"));
        }
    }
}
