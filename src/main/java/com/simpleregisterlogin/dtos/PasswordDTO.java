package com.simpleregisterlogin.dtos;

public class PasswordDTO {

    private String newPassword;

    public PasswordDTO() {
    }

    public PasswordDTO(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
