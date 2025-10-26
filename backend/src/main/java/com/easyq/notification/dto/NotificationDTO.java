package com.easyq.notification.dto;

import com.easyq.common.model.Notification;

import java.time.LocalDateTime;

public class NotificationDTO {
    
    private Long id;
    private String userName;
    private Notification.NotificationType type;
    private String title;
    private String message;
    private Boolean isSent;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    
    // Constructors
    public NotificationDTO() {}
    
    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.userName = notification.getUser().getFirstName() + " " + notification.getUser().getLastName();
        this.type = notification.getType();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.isSent = notification.getIsSent();
        this.sentAt = notification.getSentAt();
        this.createdAt = notification.getCreatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public Notification.NotificationType getType() {
        return type;
    }
    
    public void setType(Notification.NotificationType type) {
        this.type = type;
    }
    
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
    
    public Boolean getIsSent() {
        return isSent;
    }
    
    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
