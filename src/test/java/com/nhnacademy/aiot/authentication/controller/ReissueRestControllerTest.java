package com.nhnacademy.aiot.authentication.controller;

import com.nhnacademy.aiot.authentication.service.JwtService;
import com.nhnacademy.aiot.authentication.service.RedisService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ReissueRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RedisService redisService;
    @MockBean
    private JwtService jwtService;
    @Mock
    private Claims claims;

    @Test
    void reissue() throws Exception {
        String userId = "test user";
        String authority = "USER";
        String accessToken = "test access token";
        String refreshToken = "test refresh token";
        String prefix = "test prefix";
        Integer expireTime = 0;

        given(jwtService.extractClaims(anyString()))
                .willReturn(claims);
        given(claims.get("userId", String.class))
                .willReturn(userId);
        given(claims.get("authority", String.class))
                .willReturn(authority);
        given(redisService.isExist(anyString(), anyString()))
                .willReturn(true);
        given(jwtService.generateAccessToken(anyString(), anyString()))
                .willReturn(accessToken);
        given(jwtService.getPrefix())
                .willReturn(prefix);
        given(jwtService.getAccessExpiryTime())
                .willReturn(expireTime);

        mockMvc.perform(get("/api/auth/reissue")
                        .header("X-REFRESH-TOKEN", refreshToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access_token", equalTo(accessToken)))
                .andExpect(jsonPath("$.token_type", equalTo(prefix)))
                .andExpect(jsonPath("$.expire_in", equalTo(expireTime)));
    }
}