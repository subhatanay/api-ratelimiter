--lua
local key = KEYS[1]
local maxRequestAllowed = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local currentTime = redis.call("TIME")

local timePassed = currentTime[1] - window

redis.call("zremrangebyscore",key,0, timePassed)

local sizeOfKey = tonumber(redis.call('ZCARD', key))
if (sizeOfKey < maxRequestAllowed)
then
    redis.call('ZADD', key,currentTime[1], currentTime[1] )
    redis.call('EXPIREAT', key, currentTime[1] + window)
    return 1
end
return 0
