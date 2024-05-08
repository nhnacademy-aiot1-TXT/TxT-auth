package com.nhnacademy.aiot.authentication.advice;


import com.nhnacademy.aiot.authentication.controller.ReissueRestController;
import com.nhnacademy.aiot.authentication.dto.ExceptionDto;
import com.nhnacademy.aiot.authentication.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


/**
 * Reissue 예외처리 클래스
 *
 * @author jongsikk
 * @version 1.0.0
 */
@RestControllerAdvice(basePackageClasses = {ReissueRestController.class})
public class ReissueAdvice {
    /**
     * Invalid token exception handler response entity.
     *
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<ExceptionDto> invalidTokenExceptionHandler(InvalidTokenException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDto(LocalDateTime.now(), exception.getMessage()));
    }
}
