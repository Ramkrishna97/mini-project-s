package com.example.chatreactive;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private final String message;
    private final HttpStatus status;

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getMessage() { return message; }
}
