package com.easyq.common.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Entity
@Table(name = "queue_entries")
public class QueueEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @Min(value = 1, message = "Queue number must be positive")
    @Column(name = "queue_number")
    private Integer queueNumber;
    
    @Enumerated(EnumType.STRING)
    private QueueStatus status = QueueStatus.WAITING;
    
    @Column(name = "estimated_wait_time")
    private Integer estimatedWaitTime; // in minutes
    
    @Column(name = "called_at")
    private LocalDateTime calledAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum QueueStatus {
        WAITING, CALLED, IN_PROGRESS, COMPLETED, CANCELLED
    }
    
    // Constructors
    public QueueEntry() {}
    
    public QueueEntry(User user, Service service, Integer queueNumber) {
        this.user = user;
        this.service = service;
        this.queueNumber = queueNumber;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Service getService() {
        return service;
    }
    
    public void setService(Service service) {
        this.service = service;
    }
    
    public Integer getQueueNumber() {
        return queueNumber;
    }
    
    public void setQueueNumber(Integer queueNumber) {
        this.queueNumber = queueNumber;
    }
    
    public QueueStatus getStatus() {
        return status;
    }
    
    public void setStatus(QueueStatus status) {
        this.status = status;
    }
    
    public Integer getEstimatedWaitTime() {
        return estimatedWaitTime;
    }
    
    public void setEstimatedWaitTime(Integer estimatedWaitTime) {
        this.estimatedWaitTime = estimatedWaitTime;
    }
    
    public LocalDateTime getCalledAt() {
        return calledAt;
    }
    
    public void setCalledAt(LocalDateTime calledAt) {
        this.calledAt = calledAt;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
