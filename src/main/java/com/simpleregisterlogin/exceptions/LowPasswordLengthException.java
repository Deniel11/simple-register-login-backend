package com.simpleregisterlogin.exceptions;

public class LowPasswordLengthException extends RuntimeException {

    public LowPasswordLengthException(Integer length) {
        super(length.toString());
    }
}
