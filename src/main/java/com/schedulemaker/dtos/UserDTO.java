package com.schedulemaker.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class UserDTO {

    private String username;

    private String email;

    private String password;

    private Date birthdate;

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
