package com.simpleregisterlogin.dtos;

public class EmailTokenDTO {

    private String token;

    public EmailTokenDTO() {
    }

    public EmailTokenDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
