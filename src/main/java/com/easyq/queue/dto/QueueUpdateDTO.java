package com.easyq.queue.dto;

import com.easyq.common.model.QueueEntry;

public class QueueUpdateDTO {
    
    private String type; // "JOINED", "CALLED", "STARTED", "COMPLETED", "CANCELLED"
    private QueueEntryDTO queueEntry;
    private String message;
    private Long serviceId;
    
    // Constructors
    public QueueUpdateDTO() {}
    
    public QueueUpdateDTO(String type, QueueEntryDTO queueEntry, String message) {
        this.type = type;
        this.queueEntry = queueEntry;
        this.message = message;
    }
    
    public QueueUpdateDTO(String type, QueueEntryDTO queueEntry, String message, Long serviceId) {
        this.type = type;
        this.queueEntry = queueEntry;
        this.message = message;
        this.serviceId = serviceId;
    }
    
    // Getters and Setters
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public QueueEntryDTO getQueueEntry() {
        return queueEntry;
    }
    
    public void setQueueEntry(QueueEntryDTO queueEntry) {
        this.queueEntry = queueEntry;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Long getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
