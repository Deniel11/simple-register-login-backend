package com.simpleregisterlogin.dtos;

public class MessageDTO {

    private final String status;

    private final String message;

    public MessageDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
