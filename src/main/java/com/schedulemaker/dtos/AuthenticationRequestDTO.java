package com.schedulemaker.dtos;

public class AuthenticationRequestDTO {

    private String username;

    private String password;

    public AuthenticationRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}