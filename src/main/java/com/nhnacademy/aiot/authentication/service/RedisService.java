package com.nhnacademy.aiot.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private void removeOldestToken(String userId) {
        Set<String> keys = redisTemplate.keys("userId:" + userId + ":*");

        if (keys != null && keys.size() > 3) {
            String minTimestampKey = null;
            long minTimestamp = Long.MAX_VALUE;
            for (String key : keys) {
                String[] parts = key.split(":");
                long timestamp = Long.parseLong(parts[2]);

                if (timestamp < minTimestamp) {
                    minTimestamp = timestamp;
                    minTimestampKey = key;
                }
            }
            if (minTimestampKey != null) {
                redisTemplate.delete(minTimestampKey);
            }
        }
    }

    public void addRefreshToken(String userId, String refreshToken, int expiryTime) {
        removeOldestToken(userId);
        ValueOperations<String, String> valOps = redisTemplate.opsForValue();
        String key = "userId:" + userId + ":" + System.currentTimeMillis();
        valOps.set(key, refreshToken);
        redisTemplate.expire(key, expiryTime, TimeUnit.DAYS);
    }

    public void removeUserTokens(String userId) {
        Set<String> keys = redisTemplate.keys("userId:" + userId + ":*");
        if (keys != null) {
            for (String key : keys) {
                redisTemplate.delete(key);
            }
        }
    }




}
