package com.nhnacademy.aiot.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.authentication.dto.LoginRequest;
import com.nhnacademy.aiot.authentication.dto.AccessTokenResponse;
import com.nhnacademy.aiot.authentication.dto.RefreshTokenResponse;
import com.nhnacademy.aiot.authentication.security.CustomUserDetails;
import com.nhnacademy.aiot.authentication.service.JwtService;
import com.nhnacademy.aiot.authentication.service.RedisService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 로그인을 시도했을 때와 성공했을 때 사용되는 필터입니다.
 */
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

    /**
     * 로그인 요청으로 받은 아이디와 비밀 번호를 받아 UsernamePasswordAuthenticationToken을 만듭니다.
     *
     * @param request
     * @param response
     * @return {@link Authentication}를 반환합니다.
     */
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

    /**
     * 로그인이 성공했을 때 실행됩니다. payload에 user id와 authority가 담긴 access token과 refresh token을 발급하고, response body에 넣어 보냅니다.
     * refresh token은 redis에 따로 저장합니다.
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        String userId = customUserDetails.getUsername();
        String authority = "ROLE_USER";
        GrantedAuthority grantedAuthority = customUserDetails.getAuthorities().stream().findFirst().orElse(null);
        if (grantedAuthority != null) {
            authority = grantedAuthority.getAuthority();
        }

        String accessToken = jwtService.generateAccessToken(userId, authority);
        String refreshToken = jwtService.generateRefreshToken(userId, authority);

        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(accessToken, jwtService.getPrefix(), jwtService.getAccessExpiryTime());
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(refreshToken, jwtService.getRefreshExpiryTime());

        redisService.addRefreshToken(userId, refreshToken, refreshTokenResponse.getExpiresIn());

        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", accessTokenResponse);
        tokens.put("refreshToken", refreshTokenResponse);
        String result = objectMapper.writeValueAsString(tokens);

        PrintWriter printWriter = response.getWriter();
        response.setHeader("content-type", "application/json");
        printWriter.write(result);
        printWriter.close();
    }
}
