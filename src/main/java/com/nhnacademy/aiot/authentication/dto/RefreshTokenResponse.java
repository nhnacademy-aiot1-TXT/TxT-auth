package com.nhnacademy.aiot.authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenResponse {
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expire_in")
    private Integer expiresIn;
}
