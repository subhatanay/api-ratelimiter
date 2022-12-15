package com.subhadev.ratelimiter.strategy.inmemory;

import com.subhadev.ratelimiter.models.RuleParams;
import com.subhadev.ratelimiter.strategy.TokenBucketStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryTokenBucket extends TokenBucketStrategy {
    private static Logger logger = LoggerFactory.getLogger(InMemoryTokenBucket.class);
    private Map<String, BucketInfo> tokenStorage;

    public InMemoryTokenBucket() {
        logger.info("Initialized InMemoryTokenBucket");
    }
    @Override
    public boolean isRequestAllowed(String request, RuleParams params) {
        if (this.tokenStorage.get(request)!=null) {
            this.createToken(request, params.getBucketSize());
        }
        if (this.tokenStorage.get(request) != null) {
            BucketInfo bucket= this.tokenStorage.get(request);
            long timePassed = (System.currentTimeMillis() / 1000) - bucket.lastUpdatedStamp;

            int tokensToBeAdded = (int) (timePassed * params.getRefillRate());
            if (tokensToBeAdded + bucket.bucketCount.get() >= params.getBucketSize()) {
                bucket.bucketCount = new AtomicInteger( params.getBucketSize());
            } else {
                bucket.bucketCount.addAndGet(tokensToBeAdded);
            }
            bucket.lastUpdatedStamp = System.currentTimeMillis()/1000;
            return  bucket.bucketCount.get() >=0 &&  bucket.bucketCount.getAndDecrement() >= 0;
        }
        return true;
    }

    @Override
    public void createToken(String key, Object value) {
        if (this.tokenStorage == null) {
            this.tokenStorage = new HashMap<>();
        }
       this.tokenStorage.put(key, new BucketInfo(System.currentTimeMillis() / 1000,new AtomicInteger((Integer) value)));
    }

    public static class BucketInfo {
        long lastUpdatedStamp;
        AtomicInteger bucketCount;
        public BucketInfo(long time, AtomicInteger bC) {
            lastUpdatedStamp = time;
            bucketCount = bC;
        }
    }
}
