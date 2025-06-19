package com.moongchi.moongchi_be.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void setTestKey(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getTestKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}

