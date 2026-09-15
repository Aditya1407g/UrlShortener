package com.aditya.urlshortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimiter {

    private final RedisTemplate<String, String> redisTemplate;
    public RateLimiter(RedisTemplate<String,String> redisTemplate){
        this.redisTemplate=redisTemplate;
    }
    private static final Logger log = LoggerFactory.getLogger(RateLimiter.class);
    public boolean tryConsume(String key, int capacity , double refillratePerSecond){

        try{
            long now = System.currentTimeMillis();
            Map<Object, Object> state = redisTemplate.opsForHash().entries(key);

            double tokens;
            long lastRefill;

            if (state.isEmpty()) {
                tokens = capacity;
                lastRefill = now;
            } else {
                tokens = Double.parseDouble(state.get("tokens").toString());
                lastRefill = Long.parseLong(state.get("lastRefill").toString());
            }

            double elapsedSeconds = (now - lastRefill) / 1000;
            double tokensGained = elapsedSeconds * refillratePerSecond;
            double newTokens = tokens + tokensGained;

            newTokens = Math.min(newTokens, capacity);

            boolean allowed = false;
            if (newTokens >= 1) {
                newTokens--;
                allowed = true;
            }

            Map<String, String> newState = new HashMap<>();
            newState.put("tokens", String.valueOf(newTokens));
            newState.put("lastRefill", String.valueOf(now));
            redisTemplate.opsForHash().putAll(key, newState);
            redisTemplate.expire(key, Duration.ofHours(1));
            return allowed;
        } catch (Exception ex) {
          log.warn("Rate limiter failed for key {}, allowing request", key, ex);
          return true;
        }
    }
}
