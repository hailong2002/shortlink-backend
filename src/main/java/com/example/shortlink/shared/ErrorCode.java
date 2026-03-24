package com.example.shortlink.shared;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNAUTHORIZED("Must login"),;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

}
