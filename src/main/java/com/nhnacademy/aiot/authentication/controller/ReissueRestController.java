package com.nhnacademy.aiot.authentication.controller;

import com.nhnacademy.aiot.authentication.dto.AccessTokenResponse;
import com.nhnacademy.aiot.authentication.exception.InvalidTokenException;
import com.nhnacademy.aiot.authentication.service.JwtService;
import com.nhnacademy.aiot.authentication.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * access token 재발급과 관련된 컨트롤러입니다.
 *
 * @author parksangwon
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Reissue Rest Controller")
@RequestMapping("/api/auth/reissue")
public class ReissueRestController {

    private final RedisService redisService;
    private final JwtService jwtService;

    /**
     * refresh token을 검증하고, 유효한 토큰이면 access token을 재발급합니다. redis에 존재하지 않는 refresh token이면 InvalidTokenException 예외를 던집니다.
     *
     * @param refreshToken access token 재발급을 위해 필요한 refresh token
     * @return {@link AccessTokenResponse} dto를 반환합니다.
     */
    @GetMapping
    @Operation(summary = "JWT 재발급 API")
    public AccessTokenResponse reissue(@RequestHeader("X-REFRESH-TOKEN") String refreshToken) {
        String userId = jwtService.extractClaims(refreshToken).get("userId", String.class);
        String authority = jwtService.extractClaims(refreshToken).get("authority", String.class);

        if (!redisService.isExist(userId, refreshToken)) {
            throw new InvalidTokenException();
        }

        return new AccessTokenResponse(jwtService.generateAccessToken(userId, authority), jwtService.getPrefix(), jwtService.getAccessExpiryTime());
    }
}
