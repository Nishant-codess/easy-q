package com.easyq.common.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_events")
public class AnalyticsEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "event_type")
    private String eventType;
    
    @Column(name = "event_data", columnDefinition = "JSON")
    private String eventData;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public AnalyticsEvent() {}
    
    public AnalyticsEvent(String eventType, String eventData) {
        this.eventType = eventType;
        this.eventData = eventData;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getEventData() {
        return eventData;
    }
    
    public void setEventData(String eventData) {
        this.eventData = eventData;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
