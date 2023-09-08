package com.schedulemaker.dtos;

public class RegisteredUserDTO {

    private Long id;

    private String username;

    private String email;

    private String password;

    private String birthdate;

    private boolean admin;

    private boolean valid;

    public RegisteredUserDTO() {
    }

    public RegisteredUserDTO(Long id, String username, String email, String password, String birthdate, boolean admin, boolean valid) {
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
