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
    private static final String SLIDING_WINDOW_SCRIPT = "ratelimit-slidingwindow-log.lua";
    private Jedis redisClient;
    private String rateLimitScript;

    public RedisSlidingWindowLogStrategy(Jedis redisClient) throws URISyntaxException, IOException {
        this.redisClient = redisClient;
        this.loadScript();
        logger.info("Initialized RedisSlidingWindowLogStrategy and Script loaded successfully.");
    }

    private void loadScript() throws URISyntaxException, IOException {
        String script = new String(Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource(SLIDING_WINDOW_SCRIPT).toURI())));
        this.rateLimitScript = this.redisClient.scriptLoad(script);
    }
    @Override
    public boolean isRequestAllowed(String request, RuleParams params) {
        List<String> KEYS = Arrays.asList(request);
        List<String> ARGV = Arrays.asList(new String[] {String.valueOf(params.getSlidingWindowMaxRequest()), String.valueOf(params.getSlidingWindowPeriod())});
        Boolean requestAllowed = ((Long) this.redisClient.evalsha(this.rateLimitScript, KEYS, ARGV)) == 1 ? true: false ;
        return requestAllowed;
    }
}
