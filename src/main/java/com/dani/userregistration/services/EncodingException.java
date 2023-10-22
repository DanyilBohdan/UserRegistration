package com.dani.userregistration.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class EncodingException extends RuntimeException {

    public EncodingException(String message) {
        super(message);
    }
}
