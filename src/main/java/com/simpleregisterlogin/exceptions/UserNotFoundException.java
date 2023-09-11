package com.simpleregisterlogin.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Username or password is incorrect.");
    }

    public UserNotFoundException(String username) {
        super("User with name " + username + " is not found");
    }
}
