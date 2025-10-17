package com.example.bankcards.exception;

public class WrongCardLengthException extends RuntimeException {
    public WrongCardLengthException(String message) {
        super(message);
    }
}
