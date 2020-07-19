package com.github.boardyb.machinist.machine.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {

    private Integer status;
    private LocalDateTime timestamp;
    private String message;

    public ErrorResponse(Integer status, String message) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }
}
