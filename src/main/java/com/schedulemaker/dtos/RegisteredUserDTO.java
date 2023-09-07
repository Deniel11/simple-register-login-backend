package com.schedulemaker.dtos;

import java.util.Date;

public class RegisteredUserDTO {

    private final Long id;

    private final String username;

    private final String email;

    private final String password;

    private final Date birthdate;

    private final boolean admin;

    private final boolean valid;

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
