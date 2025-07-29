package com.example.partidasdefutebol.exceptions;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
    private final int statusCode;

    public ConflictException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
