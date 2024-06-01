package com.nhnacademy.aiot.authentication.exception;

/**
 * DB에서 refresh token을 찾지 못 했을 때 발생하는 InvalidTokenException 예외입니다.
 *
 * @author parksangwon
 * @version 1.0.0
 */
public class InvalidTokenException extends RuntimeException {
    /**
     * 기본 생성자 메서드
     */
    public InvalidTokenException() {
    }

    /**
     * message를 가지는 생성자 메서드
     *
     * @param message
     */
    public InvalidTokenException(String message) {
        super(message);
    }
}
