package com.nhnacademy.aiot.authentication.exception;

/**
 * token의 key 암호화 및 복호화 실패 시 발생하는 예외입니다.
 *
 * @author parksangwon
 * @version 1.0.0
 */
public class CryptoOperationException extends RuntimeException {
    /**
     * message를 가지는 생성자 메서드
     *
     * @param message
     */
    public CryptoOperationException(String message) {
        super(message);
    }
}
