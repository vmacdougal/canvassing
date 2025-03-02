package com.example.canvassing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="This address already exists")
public class DuplicateAddressException extends RuntimeException {
    public DuplicateAddressException(String message, Exception cause) {
        super("This address already exists", cause);
    }
}
