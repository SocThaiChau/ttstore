package com.example.back_end.model.dto;

import java.util.Date;


public class NotificationDTO {
    private String title;
    private String message;
    private SenderDto sender;
    private Date createdAt;
    private Long id;
    private boolean read;

    // Getters, setters, and constructors
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SenderDto getSender() {
        return sender;
    }

    public void setSender(SenderDto sender) {
        this.sender = sender;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}