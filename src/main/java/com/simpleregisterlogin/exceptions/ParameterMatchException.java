package com.simpleregisterlogin.exceptions;

public class ParameterMatchException extends RuntimeException {
    public ParameterMatchException(String parameter) {
        super(parameter);
    }
}
