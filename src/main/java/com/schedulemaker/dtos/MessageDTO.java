package com.schedulemaker.dtos;

public class MessageDTO {

    private String status;

    private String message;

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
