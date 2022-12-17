package com.subhadev.ratelimiter.strategy.redis;

import com.subhadev.ratelimiter.models.RuleParams;
import com.subhadev.ratelimiter.strategy.RateLimitingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class RedisSlidingWindowLogStrategy implements RateLimitingStrategy {
    private static Logger logger = LoggerFactory.getLogger(RedisSlidingWindowLogStrategy.class);
    private static final String SLIDING_WINDOW_SCRIPT = "--lua\n" +
            "local key = KEYS[1]\n" +
            "local maxRequestAllowed = tonumber(ARGV[1])\n" +
            "local window = tonumber(ARGV[2])\n" +
            "local currentTime = redis.call(\"TIME\")\n" +
            "\n" +
            "local timePassed = currentTime[1] - window\n" +
            "\n" +
            "redis.call(\"zremrangebyscore\",key,0, timePassed)\n" +
            "\n" +
            "local sizeOfKey = tonumber(redis.call('ZCARD', key))\n" +
            "if (sizeOfKey < maxRequestAllowed)\n" +
            "then\n" +
            "    redis.call('ZADD', key,currentTime[1], currentTime[1] )\n" +
            "    redis.call('EXPIREAT', key, currentTime[1] + window)\n" +
            "    return 1\n" +
            "end\n" +
            "return 0";
    private Jedis redisClient;
    private String rateLimitScript;

    public RedisSlidingWindowLogStrategy(Jedis redisClient) throws URISyntaxException, IOException {
        this.redisClient = redisClient;
        this.loadScript();
        logger.info("Initialized RedisSlidingWindowLogStrategy and Script loaded successfully.");
    }

    private void loadScript(){
        this.rateLimitScript = this.redisClient.scriptLoad(SLIDING_WINDOW_SCRIPT);
    }
    @Override
    public boolean isRequestAllowed(String request, RuleParams params) {
        List<String> KEYS = Arrays.asList(request);
        List<String> ARGV = Arrays.asList(new String[] {String.valueOf(params.getSlidingWindowMaxRequest()), String.valueOf(params.getSlidingWindowPeriod())});
        Boolean requestAllowed = ((Long) this.redisClient.evalsha(this.rateLimitScript, KEYS, ARGV)) == 1 ? true: false ;
        return requestAllowed;
    }
}
