package com.nhnacademy.aiot.authentication.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 예외처리 응답 DTO
 *
 * @author jongsikk
 * @version 1.0.0
 */
@Data
public class ApiExceptionDto {
    private final LocalDateTime time;
    private final String message;
}
