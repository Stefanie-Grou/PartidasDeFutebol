package com.example.partidasdefutebol.exceptions;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final int statusCode;

    public CustomException(String message) {
        super(message);
        this.statusCode = 500;
    }

    public CustomException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
