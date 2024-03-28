package com.nhnacademy.aiot.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.authentication.dto.LoginRequest;
import com.nhnacademy.aiot.authentication.dto.AccessTokenResponse;
import com.nhnacademy.aiot.authentication.dto.RefreshTokenResponse;
import com.nhnacademy.aiot.authentication.security.CustomUserDetails;
import com.nhnacademy.aiot.authentication.service.JwtService;
import com.nhnacademy.aiot.authentication.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(JwtService jwtService, ObjectMapper objectMapper, RedisService redisService, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.redisService = redisService;
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        LoginRequest loginRequest;
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Request parsing failed", e);
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword());
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        String userId = customUserDetails.getUsername();

        String accessToken = jwtService.generateAccessToken(userId);
        String refreshToken = jwtService.generateRefreshToken(userId);

        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(accessToken, jwtService.getPrefix(), jwtService.getAccessExpiryTime());
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(refreshToken, jwtService.getRefreshExpiryTime());

        redisService.addRefreshToken(userId, refreshToken, refreshTokenResponse.getExpiresIn());

        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", accessTokenResponse);
        tokens.put("refreshToken", refreshTokenResponse);
        String result = objectMapper.writeValueAsString(tokens);

        PrintWriter printWriter = response.getWriter();
        printWriter.write(result);
        printWriter.close();
    }
}
