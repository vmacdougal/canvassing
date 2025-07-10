package com.example.canvassing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="This household is not found")
public class HouseholdNotFoundException extends RuntimeException {
    public HouseholdNotFoundException(String message) {
        super("This household is not found");
    }

}
