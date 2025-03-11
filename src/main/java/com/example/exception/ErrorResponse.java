package com.example.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final String message;
    private final HttpStatus status;
    private final LocalDateTime timestamp;

    public ErrorResponse(final String message, final HttpStatus status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
