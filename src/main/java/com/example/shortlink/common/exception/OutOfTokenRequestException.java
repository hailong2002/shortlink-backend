package com.example.shortlink.common.exception;

public class OutOfTokenRequestException extends RuntimeException{
    public OutOfTokenRequestException(String message) {
        super(message);
    }
}
