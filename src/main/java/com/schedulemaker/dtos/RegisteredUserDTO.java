package com.schedulemaker.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class RegisteredUserDTO {

    private Long id;

    private String username;

    private String email;

    private String password;

    private Date birthdate;

    private boolean admin;

    private boolean valid;

    public RegisteredUserDTO(Long id, String username, String email, String password, Date birthdate, boolean admin, boolean valid) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        this.admin = admin;
        this.valid = valid;
    }

    public Long getId() {
        return id;
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

    public boolean isAdmin() {
        return admin;
    }

    public boolean isValid() {
        return valid;
    }
}
