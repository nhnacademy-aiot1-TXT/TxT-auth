package com.nhnacademy.aiot.authentication.controller;

import com.nhnacademy.aiot.authentication.dto.AccessTokenResponse;
import com.nhnacademy.aiot.authentication.exception.InvalidTokenException;
import com.nhnacademy.aiot.authentication.service.JwtService;
import com.nhnacademy.aiot.authentication.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * refresh token 재발급과 관련된 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/reissue")
public class ReissueController {

    private final RedisService redisService;
    private final JwtService jwtService;

    @GetMapping
    public AccessTokenResponse reissue(@RequestHeader("X-REFRESH-TOKEN") String refreshToken) {
        String userId = jwtService.extractClaims(refreshToken).get("userId", String.class);
        String authority = jwtService.extractClaims(refreshToken).get("authority", String.class);

        if (!redisService.isExist(userId, refreshToken)) {
            throw new InvalidTokenException();
        }

        return new AccessTokenResponse(jwtService.generateAccessToken(userId, authority), jwtService.getPrefix(), jwtService.getAccessExpiryTime());
    }
}
