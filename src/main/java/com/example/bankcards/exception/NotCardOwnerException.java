package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotCardOwnerException extends RuntimeException {
    public NotCardOwnerException(String message) {
        super(message);
    }
}
