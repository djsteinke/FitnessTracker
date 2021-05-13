package com.rn5.libstrava.exception;

public class StravaUnauthorizedException extends RuntimeException {
    private static final String message = "401:Unauthorized";
    public StravaUnauthorizedException() {}

    @Override
    public String getMessage() {
        return message;
    }
}
