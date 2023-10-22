package com.dani.userregistration.services.confirmationCode;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ExpiredConfirmationCodeException extends RuntimeException {
    public ExpiredConfirmationCodeException(String message) {
        super(message);
    }
}