package com.techinitializer.spring_security.advice;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public String handleBadCredentialsException(BadCredentialsException e) {
        return "Error: Invalid username or password.";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e) {
        return "Error: " + e.getMessage();
    }
}
