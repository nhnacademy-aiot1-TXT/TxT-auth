package com.nhnacademy.aiot.authentication.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExceptionDto {
    private final LocalDateTime time;
    private final String message;
}
