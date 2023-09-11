package com.simpleregisterlogin.dtos;

public class AuthenticationResponseDTO {

    private final String status;

    private final String token;

    public AuthenticationResponseDTO(String status, String token) {
        this.status = status;
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }
}