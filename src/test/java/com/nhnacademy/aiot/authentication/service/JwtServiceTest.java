package com.nhnacademy.aiot.authentication.service;

import com.nhnacademy.aiot.authentication.exception.CryptoOperationException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class JwtServiceTest {
    @Autowired
    private JwtService jwtService;
    private String accessToken;
    private static final String USER_ID = "test user";
    private static final String AUTHORITY = "USER";

    @BeforeEach
    void setUp() {
        accessToken = jwtService.generateAccessToken(USER_ID, AUTHORITY);
    }

    @Test
    void extractClaims() {
        Claims claims = jwtService.extractClaims(accessToken);
        String userId = claims.get("userId", String.class);
        String authority = claims.get("authority", String.class);

        assertAll(
                () -> assertNotNull(claims),
                () -> assertEquals(USER_ID, userId),
                () -> assertEquals(AUTHORITY, authority)
        );
    }

    @Test
    void generateAccessToken() {
        String generateUserId = "generate user";
        String generateAuthority = "ADMIN";

        String testToken = jwtService.generateAccessToken(generateUserId, generateAuthority);
        Claims claims = jwtService.extractClaims(testToken);
        String extractUserId = claims.get("userId", String.class);
        String extractAuthority = claims.get("authority", String.class);

        assertAll(
                () -> assertNotNull(testToken),
                () -> assertNotNull(claims),
                () -> assertEquals(generateUserId, extractUserId),
                () -> assertEquals(generateAuthority, extractAuthority)
        );
    }

    @Test
    void generateRefreshToken() {
        String generateUserId = "generate user";
        String generateAuthority = "ADMIN";

        String testToken = jwtService.generateRefreshToken(generateUserId, generateAuthority);
        Claims claims = jwtService.extractClaims(testToken);
        String extractUserId = claims.get("userId", String.class);
        String extractAuthority = claims.get("authority", String.class);

        assertAll(
                () -> assertNotNull(testToken),
                () -> assertNotNull(claims),
                () -> assertEquals(generateUserId, extractUserId),
                () -> assertEquals(generateAuthority, extractAuthority)
        );
    }

    @Test
    void getPrefix() {
        String prefix = "Bearer";

        String resultPrefix = jwtService.getPrefix();

        assertEquals(prefix, resultPrefix);
    }

    @Test
    void getTokenException() {
        String generateUserId = "generate user";
        String generateAuthority = "ADMIN";
        JwtService service = mock(JwtService.class);

        given(service.generateAccessToken(anyString(), anyString())).willThrow(new CryptoOperationException("error"));

        try {
            service.generateAccessToken(generateUserId, generateAuthority);
        } catch (CryptoOperationException e) {
            assertEquals(e.getMessage(), "error");
        }
    }
}