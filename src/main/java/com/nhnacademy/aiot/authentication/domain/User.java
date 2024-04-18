package com.nhnacademy.aiot.authentication.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * User 도메인입니다.
 *
 * @author parksangwon
 * @version 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
    private String id;
    private String password;
    private String roleName;
}
