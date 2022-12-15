--lua
local key = KEYS[1]
local bucketSize = tonumber(ARGV[1])
local refillRatePerSec = tonumber(ARGV[2])
local currentTime = redis.call("TIME")

local isKeyExists = redis.call("EXISTS", key)
local currentBucketSize = 0

if (isKeyExists == 1)
then
    local lastUpdatedTime = redis.call("HGET",key,'lastUpdated')
    currentBucketSize = redis.call("HGET",key,'bucket')
    local refilltokens = currentBucketSize + (currentTime[1] - lastUpdatedTime) * refillRatePerSec

    if (refilltokens > bucketSize)
    then
        refilltokens = bucketSize
    end
    redis.call("HSET",key,'bucket', refilltokens)
    redis.call("HSET", key, 'lastUpdated', currentTime[1])
    redis.call("HINCRBY", key, 'bucket', -1)
end

if (isKeyExists == 0)
then
    redis.call("HSET", key, 'lastUpdated', currentTime[1])
    redis.call("HSET", key, 'bucket', bucketSize)

    currentBucketSize = bucketSize
end

currentBucketSize = currentBucketSize - 1
if (currentBucketSize > 0 )
then
     return 1
end

return 0
