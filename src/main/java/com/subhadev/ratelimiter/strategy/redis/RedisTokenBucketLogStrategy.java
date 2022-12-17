package com.subhadev.ratelimiter.strategy.redis;

import com.subhadev.ratelimiter.models.RuleParams;
import com.subhadev.ratelimiter.strategy.RateLimitingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.List;

public class RedisTokenBucketLogStrategy implements RateLimitingStrategy {
    private static Logger logger = LoggerFactory.getLogger(RedisTokenBucketLogStrategy.class);
    private static final String TOKEN_BUCKET_SCRIPT = "--lua\n" +
            "local key = KEYS[1]\n" +
            "local bucketSize = tonumber(ARGV[1])\n" +
            "local refillRatePerSec = tonumber(ARGV[2])\n" +
            "local currentTime = redis.call(\"TIME\")\n" +
            "\n" +
            "local isKeyExists = redis.call(\"EXISTS\", key)\n" +
            "local currentBucketSize = 0\n" +
            "\n" +
            "if (isKeyExists == 1)\n" +
            "then\n" +
            "    local lastUpdatedTime = redis.call(\"HGET\",key,'lastUpdated')\n" +
            "    currentBucketSize = redis.call(\"HGET\",key,'bucket')\n" +
            "    local refilltokens = currentBucketSize + (currentTime[1] - lastUpdatedTime) * refillRatePerSec\n" +
            "\n" +
            "    if (refilltokens > bucketSize)\n" +
            "    then\n" +
            "        refilltokens = bucketSize\n" +
            "    end\n" +
            "    redis.call(\"HSET\",key,'bucket', refilltokens)\n" +
            "    redis.call(\"HSET\", key, 'lastUpdated', currentTime[1])\n" +
            "    redis.call(\"HINCRBY\", key, 'bucket', -1)\n" +
            "end\n" +
            "\n" +
            "if (isKeyExists == 0)\n" +
            "then\n" +
            "    redis.call(\"HSET\", key, 'lastUpdated', currentTime[1])\n" +
            "    redis.call(\"HSET\", key, 'bucket', bucketSize)\n" +
            "\n" +
            "    currentBucketSize = bucketSize\n" +
            "end\n" +
            "\n" +
            "currentBucketSize = currentBucketSize - 1\n" +
            "if (currentBucketSize > 0 )\n" +
            "then\n" +
            "     return 1\n" +
            "end\n" +
            "\n" +
            "return 0\n";
    private Jedis redisClient;
    private String rateLimitScript;
    public RedisTokenBucketLogStrategy(Jedis redisClient) {
        this.redisClient = redisClient;
        this.loadScript();
        logger.info("Initialized RedisTokenBucketLogStrategy and Script loaded successfully.");
    }
    private void loadScript() {
        this.rateLimitScript = this.redisClient.scriptLoad(TOKEN_BUCKET_SCRIPT);
    }
    @Override
    public boolean isRequestAllowed(String request, RuleParams params) {
        List<String> KEYS = Arrays.asList(request);
        List<String> ARGV = Arrays.asList(new String[] {String.valueOf(params.getBucketSize()), String.valueOf(params.getRefillRate())});
        Boolean requestAllowed = ((Long) this.redisClient.evalsha(this.rateLimitScript, KEYS, ARGV)) == 1 ? true: false ;
        return requestAllowed;
    }
}
