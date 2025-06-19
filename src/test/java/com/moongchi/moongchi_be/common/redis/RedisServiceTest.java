package com.moongchi.moongchi_be.common.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // 혹은 default
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Test
    void redisConnectionTest() {
        // given
        String key = "redis:test";
        String value = "연결성_테스트";

        // when
        redisService.setTestKey(key, value);
        String result = redisService.getTestKey(key);

        // then
        Assertions.assertEquals(value, result);
    }
}
