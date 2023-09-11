package com.simpleregisterlogin.exceptions;

public class ParameterTakenException extends RuntimeException {

    public ParameterTakenException(String parameter) {
        super(parameter + " is already taken");
    }
}
