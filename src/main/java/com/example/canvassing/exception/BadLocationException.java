package com.example.canvassing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="This address has an invalid location")
public class BadLocationException extends RuntimeException {
    public BadLocationException(String message, Exception cause) {
        super("This location is not a valid latitude/longitude", cause);
    }

}
