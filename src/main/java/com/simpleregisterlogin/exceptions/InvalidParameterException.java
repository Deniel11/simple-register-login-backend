package com.simpleregisterlogin.exceptions;

public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException(String parameter) {
        super(parameter);
    }
}
