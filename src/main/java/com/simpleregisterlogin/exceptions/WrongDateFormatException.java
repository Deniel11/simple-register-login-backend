package com.simpleregisterlogin.exceptions;

public class WrongDateFormatException extends RuntimeException {

    public WrongDateFormatException() {
        super("Accepted date format: dd-mm-yyyy");
    }
}
