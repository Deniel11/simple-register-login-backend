package com.simpleregisterlogin.exceptions;

public class UserNotFoundException extends RuntimeException {

    private boolean isMessage;

    public UserNotFoundException() {
    }

    public UserNotFoundException(String username) {
        super(username);
        isMessage = true;
    }

    public UserNotFoundException(Long id) {
        super(id.toString());
        isMessage = false;
    }

    public boolean isMessage() {
        return isMessage;
    }
}
