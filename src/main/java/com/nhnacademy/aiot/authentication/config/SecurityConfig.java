package com.nhnacademy.aiot.authentication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.authentication.adapter.UserAdapter;
import com.nhnacademy.aiot.authentication.filter.JwtAuthenticationFilter;
import com.nhnacademy.aiot.authentication.security.CustomUserDetailsService;
import com.nhnacademy.aiot.authentication.service.JwtService;
import com.nhnacademy.aiot.authentication.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;

/**
 * Spring Security 관련 설정
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserAdapter userAdapter;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;
    private final AuthenticationConfiguration authenticationConfiguration;


    /**
     * 로그인, 로그아웃 등을 설정합니다. 로그아웃 시 redis에 저장된 회원의 refresh token을 삭제합니다.
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .and()
            .formLogin().disable()
            .httpBasic().disable()
            .addFilterAt(new JwtAuthenticationFilter(jwtService, objectMapper, redisService, authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests()
            .antMatchers("/api/auth/login").permitAll()
            .and()
            .authenticationProvider(authenticationProvider())
            .logout().logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(((request, response, authentication) -> {
                ServletInputStream inputStream = request.getInputStream();
                byte[] rawData = StreamUtils.copyToByteArray(inputStream);
                String refreshToken = new String(rawData);

                String userId = jwtService.extractClaims(refreshToken).get("userId", String.class);

                redisService.deleteUserToken(userId, refreshToken);
            }));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService(userAdapter);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService());
        return daoAuthenticationProvider;
    }
}
