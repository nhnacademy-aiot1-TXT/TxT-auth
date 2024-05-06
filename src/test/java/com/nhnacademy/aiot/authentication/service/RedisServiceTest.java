package com.nhnacademy.aiot.authentication.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisService redisService;

    @Test
    void addRefreshToken() {
        String userId = "testUser";
        String refreshToken = "newRefreshToken";
        int expiryTime = 1;

        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        redisService.addRefreshToken(userId, refreshToken, expiryTime);

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set(any(String.class), eq(refreshToken));
        verify(redisTemplate, times(1)).expire(anyString(), anyLong(), eq(TimeUnit.DAYS));
    }

    @Test
    void isExist() {
        String userId = "testUser";
        String refreshToken = "existingRefreshToken";

        Set<String> keys = new HashSet<>();
        keys.add("userId:" + userId + ":1234567890");
        given(redisTemplate.keys(any(String.class))).willReturn(keys);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(any(String.class))).willReturn(refreshToken);

        boolean result = redisService.isExist(userId, refreshToken);

        assertTrue(result);
    }

    @Test
    void deleteUserToken() {
        String userId = "testUser";
        String refreshToken = "existingRefreshToken";

        Set<String> keys = new HashSet<>();
        keys.add("userId:" + userId + ":1234567890");
        given(redisTemplate.keys(any(String.class))).willReturn(keys);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(any(String.class))).willReturn(refreshToken);

        redisService.deleteUserToken(userId, refreshToken);

        verify(redisTemplate, times(1)).delete(any(String.class));
    }
}