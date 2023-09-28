package com.simpleregisterlogin.exceptions;

public class EmailAddressNotFoundException extends RuntimeException {
    public EmailAddressNotFoundException(String email) {
        super(email);
    }
}
