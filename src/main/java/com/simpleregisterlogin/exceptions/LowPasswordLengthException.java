package com.simpleregisterlogin.exceptions;

public class LowPasswordLengthException extends RuntimeException {

    public LowPasswordLengthException(int length) {
        super("Password must be " + length + " characters.");
    }
}
