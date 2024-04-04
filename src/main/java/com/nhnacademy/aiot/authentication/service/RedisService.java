package com.nhnacademy.aiot.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * token이 저장된 redis와 관련된 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * user id로 저장된 token이 3개를 초과했을 때, 가장 오래된 token을 삭제합니다.
     *
     * @param userId token을 삭제할 user의 id
     */
    private void removeOldestToken(String userId) {
        Set<String> keys = getKeys(userId);
        if (keys!= null && keys.size() > 2) {
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

    /**
     * redis에 refresh token을 저장합니다.
     * key는 'userId:{userId 값}:{token을 생성한 시간}' 형태이며, value에 token 값이 들어갑니다.
     *
     * @param userId refresh token을 발급 받은 user id
     * @param refreshToken 저장할 refresh token
     * @param expiryTime reresh token의 만료 시간
     */
    public void addRefreshToken(String userId, String refreshToken, int expiryTime) {
        removeOldestToken(userId);
        ValueOperations<String, String> valOps = redisTemplate.opsForValue();
        String key = "userId:" + userId + ":" + System.currentTimeMillis();
        valOps.set(key, refreshToken);
        redisTemplate.expire(key, expiryTime, TimeUnit.DAYS);
    }

    /**
     * redis에 특정 user의 refresh token이 저장되어있는지 확인합니다.
     *
     * @param userId 검색할 user id
     * @param refreshToken 검색할 refresh token
     * @return token 존재 여부를 반환합니다.
     */
    public boolean isExist(String userId, String refreshToken) {
        Set<String> keys = getKeys(userId);
        boolean result = false;
        for (String key : keys) {
            if (refreshToken.equals(redisTemplate.opsForValue().get(key))) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 해당 user의 id로 저장된 refresh token의 key를 모두 불러옵니다.
     *
     * @param userId 검색할 user id
     * @return key값이 담긴 {@link Set}를 반환합니다.
     */
    private Set<String> getKeys(String userId) {
        return redisTemplate.keys("userId:" + userId + ":*");
    }

    /**
     * 특정 user의 refresh token을 redis에서 삭제합니다.
     *
     * @param userId 검색할 user id
     * @param refreshToken 지우고 싶은 refresh token
     */
    public void deleteUserToken(String userId, String refreshToken) {
        Set<String> keys = getKeys(userId);
        for (String key : keys) {
            if (refreshToken.equals(redisTemplate.opsForValue().get(key))) {
                redisTemplate.delete(key);
            }
        }
    }

}
