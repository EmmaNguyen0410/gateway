package com.emmang.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> invalidTokenExceptionHandling(InvalidTokenException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MissingAuthorizationException.class)
    public ResponseEntity<?> missingAuthorizationExceptionHandling(MissingAuthorizationException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
}
