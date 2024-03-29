package com.nhnacademy.aiot.authentication.controller;

import com.nhnacademy.aiot.authentication.dto.AccessTokenResponse;
import com.nhnacademy.aiot.authentication.exception.InvalidTokenException;
import com.nhnacademy.aiot.authentication.service.JwtService;
import com.nhnacademy.aiot.authentication.service.RedisService;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        String userId;
        try {
            userId = jwtService.extractClaims(refreshToken).get("userId", String.class);
        } catch (SignatureException e) {
            userId = ((UserDetails) SecurityContextHolder.getContext()
                                                         .getAuthentication()
                                                         .getPrincipal()).getUsername();
            redisService.removeUserTokens(userId);
            log.error(userId + ": refresh token 변조가 의심되어 해당 사용자의 refresh token을 모두 삭제하였습니다.");
            throw new InvalidTokenException();
        }

        return new AccessTokenResponse(jwtService.generateAccessToken(userId), jwtService.getPrefix(), jwtService.getAccessExpiryTime());
    }
}
