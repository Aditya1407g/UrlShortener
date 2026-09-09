package com.aditya.urlshortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class UrlCacheService {
    private static final Logger log = LoggerFactory.getLogger(UrlCacheService.class);
    private static final String KEY_PREFIX = "shortcode:";
    private static final Duration TTL = Duration.ofHours(24);

    private final RedisTemplate<String , String> redisTemplate;

    private UrlCacheService(RedisTemplate<String , String> redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    public Optional<String> gelLongUrl(String shortCode){
        try {
            String longUrl = redisTemplate.opsForValue().get(KEY_PREFIX + shortCode);
            return Optional.ofNullable(longUrl);
        } catch (Exception ex){
            log.warn("Redis GET failed for {}, falling back to Postgres", shortCode, ex);
            return Optional.empty();
        }
    }

    public void put(String shortCode, String longUrl){
        if(longUrl == null){
            return;
        }
        try{
            redisTemplate.opsForValue().set(KEY_PREFIX+shortCode, longUrl, TTL);
        }catch (Exception ex){
            log.warn("Redis SET failed for {}, cache miss will persist" , shortCode, ex);
        }
    }

}
