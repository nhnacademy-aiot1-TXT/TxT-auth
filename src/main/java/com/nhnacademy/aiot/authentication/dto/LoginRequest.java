package com.nhnacademy.aiot.authentication.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * login 요청이 들어왔을 때 사용하는 dto입니다.
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {
    private  String id;
    private  String password;
}
