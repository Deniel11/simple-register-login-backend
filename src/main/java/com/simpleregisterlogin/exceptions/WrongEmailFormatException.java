package com.simpleregisterlogin.exceptions;

public class WrongEmailFormatException extends RuntimeException {

    public WrongEmailFormatException() {
        super("Wrong email format");
    }
}
