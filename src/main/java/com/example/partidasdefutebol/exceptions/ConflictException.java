package com.example.partidasdefutebol.exceptions;

public class ConflictException extends RuntimeException {
    private final int statusCode;

    public ConflictException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
