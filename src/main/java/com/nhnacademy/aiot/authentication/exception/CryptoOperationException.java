package com.nhnacademy.aiot.authentication.exception;

/**
 * token의 key 암호화 및 복호화 실패 시 발생하는 예외입니다.
 */
public class CryptoOperationException extends RuntimeException{
    public CryptoOperationException(String message) {
        super(message);
    }
}
