package com.easyq.queue.dto;

import com.easyq.common.model.QueueEntry;

import java.time.LocalDateTime;

public class QueueEntryDTO {
    
    private Long id;
    private String userName;
    private String serviceName;
    private Integer queueNumber;
    private QueueEntry.QueueStatus status;
    private Integer estimatedWaitTime;
    private Integer peopleAhead;
    private Integer averageWaitTime;
    private LocalDateTime calledAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    
    // Constructors
    public QueueEntryDTO() {}
    
    public QueueEntryDTO(QueueEntry queueEntry) {
        this.id = queueEntry.getId();
        this.userName = queueEntry.getUser().getFirstName() + " " + queueEntry.getUser().getLastName();
        this.serviceName = queueEntry.getService().getName();
        this.queueNumber = queueEntry.getQueueNumber();
        this.status = queueEntry.getStatus();
        this.estimatedWaitTime = queueEntry.getEstimatedWaitTime();
        this.calledAt = queueEntry.getCalledAt();
        this.startedAt = queueEntry.getStartedAt();
        this.completedAt = queueEntry.getCompletedAt();
        this.createdAt = queueEntry.getCreatedAt();
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
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public Integer getQueueNumber() {
        return queueNumber;
    }
    
    public void setQueueNumber(Integer queueNumber) {
        this.queueNumber = queueNumber;
    }
    
    public QueueEntry.QueueStatus getStatus() {
        return status;
    }
    
    public void setStatus(QueueEntry.QueueStatus status) {
        this.status = status;
    }
    
    public Integer getEstimatedWaitTime() {
        return estimatedWaitTime;
    }
    
    public void setEstimatedWaitTime(Integer estimatedWaitTime) {
        this.estimatedWaitTime = estimatedWaitTime;
    }
    
    public Integer getPeopleAhead() {
        return peopleAhead;
    }
    
    public void setPeopleAhead(Integer peopleAhead) {
        this.peopleAhead = peopleAhead;
    }
    
    public Integer getAverageWaitTime() {
        return averageWaitTime;
    }
    
    public void setAverageWaitTime(Integer averageWaitTime) {
        this.averageWaitTime = averageWaitTime;
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
}
