package com.simpleregisterlogin.exceptions;

public class CustomAccessDeniedException extends RuntimeException {

    public CustomAccessDeniedException() {
        super("Access Denied");
    }

    public CustomAccessDeniedException(String message) {
        super("Access Denied: " + message);
    }
}
