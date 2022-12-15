package com.subhadev.ratelimiter.strategy.inmemory;

import com.subhadev.ratelimiter.models.RuleParams;
import com.subhadev.ratelimiter.strategy.SlidingWindowStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySlidingWindow extends SlidingWindowStrategy {
    private static Logger logger = LoggerFactory.getLogger(InMemorySlidingWindow.class);
    private Map<String, PriorityQueue<Long>> slidingWindowLogMap;

    public InMemorySlidingWindow() {
        logger.info("Initialized InMemorySlidingWindow");
    }

    @Override
    public boolean isRequestAllowed(String request, RuleParams params) {
        this.createToken(request);
        if (slidingWindowLogMap.get(request) != null) {
            synchronized (request) {
                PriorityQueue<Long> slidingWindowLog = slidingWindowLogMap.get(request);
                int window = params.getSlidingWindowPeriod();
                long timePassed = System.currentTimeMillis()/1000 - window;

                trimWindow(slidingWindowLog, 0l, timePassed);

                if (slidingWindowLog.size() < params.getSlidingWindowMaxRequest()) {
                    slidingWindowLog.add(System.currentTimeMillis() / 1000);
                    return true;
                }
            }
        }
        return false;
    }

    private void trimWindow(PriorityQueue<Long> slidingWindowLog, long start, long end) {
        while (!slidingWindowLog.isEmpty()) {
            long curr = slidingWindowLog.peek();
            if (curr >=start && curr <=end) {
                slidingWindowLog.poll();
                continue;
            }
            break;
        }
    }

    @Override
    public void createToken(String key) {
        if (slidingWindowLogMap == null) {
            slidingWindowLogMap = new ConcurrentHashMap<>();
        }
        if (slidingWindowLogMap.get(key) == null) {
            slidingWindowLogMap.put(key, new PriorityQueue<>());
        }
    }
}
