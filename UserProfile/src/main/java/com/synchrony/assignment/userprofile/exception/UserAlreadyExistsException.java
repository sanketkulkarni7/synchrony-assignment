package com.synchrony.assignment.userprofile.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends RuntimeException {
    private String message;

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
