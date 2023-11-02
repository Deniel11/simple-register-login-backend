package com.simpleregisterlogin.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "email"})})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private String dateOfBirth;

    private boolean admin;

    private boolean verified;

    private String verificationToken;

    private boolean enabled;

    private String forgotPasswordToken;

    private Long forgotPasswordRequestTime;

    public User() {
    }

    public User(String username, String email, String password, String dateOfBirth) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        admin = false;
        verified = false;
        enabled = false;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean valid) {
        this.verified = valid;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDateOfBirth(String birthdate) {
        this.dateOfBirth = birthdate;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getForgotPasswordToken() {
        return forgotPasswordToken;
    }

    public void setForgotPasswordToken(String forgotPasswordToken) {
        this.forgotPasswordToken = forgotPasswordToken;
    }

    public Long getForgotPasswordRequestTime() {
        return forgotPasswordRequestTime;
    }

    public void setForgotPasswordRequestTime(Long forgotPasswordRequestTime) {
        this.forgotPasswordRequestTime = forgotPasswordRequestTime;
    }
}
