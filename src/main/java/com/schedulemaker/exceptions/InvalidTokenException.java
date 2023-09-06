package com.schedulemaker.exceptions;

import io.jsonwebtoken.SignatureException;

public class InvalidTokenException extends SignatureException {

    public InvalidTokenException(String message) {
        super(message);
    }
}