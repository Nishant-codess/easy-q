package com.easyq.queue.dto;
// this is used to update the imformation for example like called ke liye , yaa in progress basically to update the changes
import com.easyq.common.model.QueueEntry;

public class QueueUpdateDTO {
    
    private String type; // "JOINED", "CALLED", "STARTED", "COMPLETED", "CANCELLED"
    private QueueEntryDTO queueEntry;
    private String message;
    private Long serviceId;
    
    // Constructors
    public QueueUpdateDTO() {}
    
    public QueueUpdateDTO(String type, QueueEntryDTO queueEntry, String message) { //this is without the service id, i have kept if there is no specific service id user demand imformation for the queue or etc
        this.type = type;
        this.queueEntry = queueEntry;
        this.message = message;
    }
    
    public QueueUpdateDTO(String type, QueueEntryDTO queueEntry, String message, Long serviceId) { // when there is specific servive id like billing etc
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
