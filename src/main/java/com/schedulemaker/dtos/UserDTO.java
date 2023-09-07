package com.schedulemaker.dtos;

import java.util.Date;

public class UserDTO {

    private final String username;

    private final String email;

    private final String password;

    private final Date birthdate;

    public UserDTO(String username, String email, String password, Date birthdate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Date getBirthdate() {
        return birthdate;
    }
}
