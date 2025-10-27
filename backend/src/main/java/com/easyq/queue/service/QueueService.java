package com.easyq.queue.service;

import com.easyq.queue.dto.QueueEntryDTO;
import com.easyq.queue.dto.QueueUpdateDTO;
import com.easyq.queue.repository.QueueEntryRepository;
import com.easyq.common.model.QueueEntry;
import com.easyq.common.model.Service;
import com.easyq.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class QueueService {
    
    @Autowired
    private QueueEntryRepository queueEntryRepository;
    
    @Autowired
    private com.easyq.admin.repository.ServiceRepository serviceRepository;
    
    @Autowired
    private com.easyq.admin.repository.UserRepository userRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public QueueEntryDTO joinQueue(Long userId, Long serviceId) {
        try {
            // Validate user and service
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
            
            if (userOpt.isEmpty() || serviceOpt.isEmpty()) {
                throw new RuntimeException("User or service not found");
            }
            
            User user = userOpt.get();
            Service service = serviceOpt.get();
            
            // Check if user is already in queue for this service
            List<QueueEntry> activeEntries = queueEntryRepository.findActiveQueueEntriesByUser(user);
            boolean alreadyInQueue = activeEntries.stream()
                .anyMatch(entry -> entry.getService().getId().equals(serviceId) && 
                         (entry.getStatus() == QueueEntry.QueueStatus.WAITING || 
                          entry.getStatus() == QueueEntry.QueueStatus.CALLED));
            
            if (alreadyInQueue) {
                throw new RuntimeException("User is already in queue for this service");
            }
            
            // Get next queue number
            Optional<Integer> maxQueueNumber = queueEntryRepository.findMaxQueueNumberByService(serviceId);
            Integer nextQueueNumber = maxQueueNumber.orElse(0) + 1;
            
            // Create queue entry
            QueueEntry queueEntry = new QueueEntry();
            queueEntry.setUser(user);
            queueEntry.setService(service);
            queueEntry.setQueueNumber(nextQueueNumber);
            queueEntry.setStatus(QueueEntry.QueueStatus.WAITING);
            queueEntry.setEstimatedWaitTime(calculateEstimatedWaitTime(serviceId));
            
            QueueEntry savedEntry = queueEntryRepository.save(queueEntry);
            
            // Broadcast queue update
            QueueUpdateDTO update = new QueueUpdateDTO(
                "JOINED", 
                new QueueEntryDTO(savedEntry), 
                user.getFirstName() + " " + user.getLastName() + " joined the queue",
                serviceId
            );
            messagingTemplate.convertAndSend("/topic/queue", update);
            
            return new QueueEntryDTO(savedEntry);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to join queue: " + e.getMessage());
        }
    }
    
    public QueueEntryDTO callNext(Long serviceId) {
        try {
            List<QueueEntry> waitingEntries = queueEntryRepository.findWaitingQueueEntriesByService(serviceId);
            if (waitingEntries.isEmpty()) {
                throw new RuntimeException("No one waiting in queue");
            }
            
            QueueEntry nextEntry = waitingEntries.get(0);
            nextEntry.setStatus(QueueEntry.QueueStatus.CALLED);
            nextEntry.setCalledAt(LocalDateTime.now());
            
            QueueEntry savedEntry = queueEntryRepository.save(nextEntry);
            
            // Broadcast queue update
            QueueUpdateDTO update = new QueueUpdateDTO(
                "CALLED", 
                new QueueEntryDTO(savedEntry), 
                "Queue number " + savedEntry.getQueueNumber() + " is being called",
                serviceId
            );
            messagingTemplate.convertAndSend("/topic/queue", update);
            
            return new QueueEntryDTO(savedEntry);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to call next: " + e.getMessage());
        }
    }
    
    public QueueEntryDTO startService(Long queueEntryId) {
        try {
            Optional<QueueEntry> entryOpt = queueEntryRepository.findById(queueEntryId);
            if (entryOpt.isEmpty()) {
                throw new RuntimeException("Queue entry not found");
            }
            
            QueueEntry entry = entryOpt.get();
            entry.setStatus(QueueEntry.QueueStatus.IN_PROGRESS);
            entry.setStartedAt(LocalDateTime.now());
            
            QueueEntry savedEntry = queueEntryRepository.save(entry);
            
            // Broadcast queue update
            QueueUpdateDTO update = new QueueUpdateDTO(
                "STARTED", 
                new QueueEntryDTO(savedEntry), 
                "Service started for queue number " + savedEntry.getQueueNumber(),
                savedEntry.getService().getId()
            );
            messagingTemplate.convertAndSend("/topic/queue", update);
            
            return new QueueEntryDTO(savedEntry);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to start service: " + e.getMessage());
        }
    }
    
    public QueueEntryDTO completeService(Long queueEntryId) {
        try {
            Optional<QueueEntry> entryOpt = queueEntryRepository.findById(queueEntryId);
            if (entryOpt.isEmpty()) {
                throw new RuntimeException("Queue entry not found");
            }
            
            QueueEntry entry = entryOpt.get();
            entry.setStatus(QueueEntry.QueueStatus.COMPLETED);
            entry.setCompletedAt(LocalDateTime.now());
            
            QueueEntry savedEntry = queueEntryRepository.save(entry);
            
            // Broadcast queue update
            QueueUpdateDTO update = new QueueUpdateDTO(
                "COMPLETED", 
                new QueueEntryDTO(savedEntry), 
                "Service completed for queue number " + savedEntry.getQueueNumber(),
                savedEntry.getService().getId()
            );
            messagingTemplate.convertAndSend("/topic/queue", update);
            
            return new QueueEntryDTO(savedEntry);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to complete service: " + e.getMessage());
        }
    }
    
    public List<QueueEntryDTO> getQueueEntries(Long serviceId) {
        List<QueueEntry> entries;
        if (serviceId != null) {
            entries = queueEntryRepository.findWaitingQueueEntriesByService(serviceId);
        } else {
            entries = queueEntryRepository.findWaitingQueueEntries();
        }
        
        return entries.stream()
                .map(QueueEntryDTO::new)
                .collect(Collectors.toList());
    }
    
    public List<QueueEntryDTO> getUserQueueEntries(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        
        List<QueueEntry> entries = queueEntryRepository.findByUser(userOpt.get());
        return entries.stream()
                .map(QueueEntryDTO::new)
                .collect(Collectors.toList());
    }
    
    private Integer calculateEstimatedWaitTime(Long serviceId) {
        // Simple calculation: average service duration * number of people in queue
        List<QueueEntry> waitingEntries = queueEntryRepository.findWaitingQueueEntriesByService(serviceId);
        Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
        
        if (serviceOpt.isEmpty()) {
            return 30; // Default 30 minutes
        }
        
        int averageDuration = serviceOpt.get().getDurationMinutes();
        int peopleInQueue = waitingEntries.size();
        
        return averageDuration * peopleInQueue;
    }
}
