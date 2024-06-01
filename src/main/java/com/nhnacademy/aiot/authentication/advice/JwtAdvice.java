package com.nhnacademy.aiot.authentication.advice;

import com.nhnacademy.aiot.authentication.dto.ApiExceptionDto;
import com.nhnacademy.aiot.authentication.exception.CryptoOperationException;
import com.nhnacademy.aiot.authentication.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Jwt 관련 예외처리 클래스
 *
 * @author jongsikk
 * @version 1.0.0
 */
@RestControllerAdvice(basePackageClasses = {JwtService.class})
public class JwtAdvice {
    /**
     * Crypto operation exception handler response entity.
     *
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler(value = CryptoOperationException.class)
    public ResponseEntity<ApiExceptionDto> cryptoOperationExceptionHandler(CryptoOperationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiExceptionDto(LocalDateTime.now(), exception.getMessage()));
    }
}
