package com.emmang.gateway.exception;

public class MissingAuthorizationException extends RuntimeException {
    public MissingAuthorizationException(String message) {
        super(message);
    }
}
