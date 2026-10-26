package com.example.shortlink.shared;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNAUTHORIZED("User have not logged in yet !", 401),
    SHORTENED_CODE_NOT_FOUND("Shortened code not found", 400),
    INVALID_URL("Entered url is invalid", 400),
    OUT_OF_TOKEN_REQUEST("Please wait a few minute to continue", 400);
    private final String message;
    private final Integer status;

    ErrorCode(String message, Integer status) {
        this.message = message;
        this.status = status;
    }

}
