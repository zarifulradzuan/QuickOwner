package com.example.quickowner.model;

public class NotificationMessage {
    private String idMessage;
    private String message;
    private String idPlace;


    public NotificationMessage() {
    }

    public NotificationMessage(String idPlace, String message) {
        this.idPlace = idPlace;
        this.message = message;
    }

    public String getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(String idPlace) {
        this.idPlace = idPlace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }
}
