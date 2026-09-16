package com.aditya.urlshortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateLimiter {

    private static final Logger log = LoggerFactory.getLogger(RateLimiter.class);

    private static final long TTL_MILLIS = 3_600_000L;

    /**
     * Token bucket, evaluated atomically inside Redis.
     *
     * Running the read-modify-write as a script matters: with a separate
     * HGETALL then HSET, two concurrent requests can both read the same token
     * count and both be allowed, so the limit leaks under exactly the
     * concurrency it is meant to control.
     */
    private static final RedisScript<Long> TOKEN_BUCKET = new DefaultRedisScript<>("""
            local tokens     = tonumber(redis.call('HGET', KEYS[1], 'tokens'))
            local lastRefill = tonumber(redis.call('HGET', KEYS[1], 'lastRefill'))
            local capacity   = tonumber(ARGV[1])
            local refillRate = tonumber(ARGV[2])
            local now        = tonumber(ARGV[3])
            local ttlMillis  = tonumber(ARGV[4])

            if tokens == nil or lastRefill == nil then
                tokens = capacity
                lastRefill = now
            end

            -- Float division, so a sub-second gap still earns a fractional
            -- token. Integer division here would round every gap under one
            -- second down to zero, and because lastRefill advances on every
            -- call the bucket would then never refill under sustained traffic.
            local elapsedSeconds = (now - lastRefill) / 1000.0
            tokens = math.min(capacity, tokens + elapsedSeconds * refillRate)

            local allowed = 0
            if tokens >= 1 then
                tokens = tokens - 1
                allowed = 1
            end

            redis.call('HSET', KEYS[1], 'tokens', tokens, 'lastRefill', now)
            redis.call('PEXPIRE', KEYS[1], ttlMillis)
            return allowed
            """, Long.class);

    private final RedisTemplate<String, String> redisTemplate;

    public RateLimiter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryConsume(String key, int capacity, double refillratePerSecond) {
        try {
            Long allowed = redisTemplate.execute(
                    TOKEN_BUCKET,
                    List.of(key),
                    String.valueOf(capacity),
                    String.valueOf(refillratePerSecond),
                    String.valueOf(System.currentTimeMillis()),
                    String.valueOf(TTL_MILLIS));

            // Fail open: a null reply means the script did not run cleanly,
            // and a rate limiter should not take the API down with it.
            return allowed == null || allowed == 1L;
        } catch (Exception ex) {
            log.warn("Rate limiter failed for key {}, allowing request", key, ex);
            return true;
        }
    }
}
