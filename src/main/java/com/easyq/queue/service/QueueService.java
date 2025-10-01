package com.easyq.queue.service;

import com.easyq.queue.dto.QueueEntryDTO;
import com.easyq.queue.dto.QueueUpdateDTO;
import com.easyq.admin.repository.QueueEntryRepository;
import com.easyq.common.model.QueueEntry;
import com.easyq.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
            validateIds(userId, serviceId);

            User user = resolveOrCreateDemoUser(userId);
            com.easyq.common.model.Service service = getServiceOrThrow(serviceId);

            // Prevent duplicates only for real users
            if (userId != null && isAlreadyQueuedForService(user, serviceId)) {
                throw new IllegalStateException("User is already in queue for this service");
            }

            int nextQueueNumber = queueEntryRepository
                    .findMaxQueueNumberByService(serviceId)
                    .orElse(0) + 1;

            QueueEntry queueEntry = new QueueEntry();
            queueEntry.setUser(user);
            queueEntry.setService(service);
            queueEntry.setQueueNumber(nextQueueNumber);
            queueEntry.setStatus(QueueEntry.QueueStatus.WAITING);
            queueEntry.setEstimatedWaitTime(calculateEstimatedWaitTime(serviceId));

            QueueEntry savedEntry = queueEntryRepository.save(queueEntry);

            QueueUpdateDTO update = new QueueUpdateDTO(
                "JOINED",
                enrichQueueEntryDTO(savedEntry),
                user.getFirstName() + " " + user.getLastName() + " joined the queue",
                serviceId
            );
            messagingTemplate.convertAndSend("/topic/queue", update);

            return enrichQueueEntryDTO(savedEntry);
        } catch (Exception e) {
            throw new RuntimeException("Failed to join queue: " + e.getMessage());
        }
    }

    private User resolveOrCreateDemoUser(Long userId) {
        if (userId != null) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
        // Create a simple unique demo user each time to allow multiple joins in a demo
        String suffix = String.valueOf(System.currentTimeMillis());
        String username = "customer_" + suffix;
        User demo = new User(username, username + "@example.com", "password", "Customer", suffix);
        demo.setRole(User.Role.CUSTOMER);
        return userRepository.save(demo);
    }

    private com.easyq.common.model.Service getServiceOrThrow(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
    }

    private boolean isAlreadyQueuedForService(User user, Long serviceId) {
        List<QueueEntry> activeEntries = queueEntryRepository.findActiveQueueEntriesByUser(user);
        return activeEntries.stream().anyMatch(entry ->
                entry.getService().getId().equals(serviceId) &&
                        (entry.getStatus() == QueueEntry.QueueStatus.WAITING ||
                         entry.getStatus() == QueueEntry.QueueStatus.CALLED));
    }
    
    public QueueEntryDTO callNext(Long serviceId) {
        try {
            validateIds(1L, serviceId); 
            List<QueueEntry> waitingEntries = queueEntryRepository.findWaitingQueueEntriesByService(serviceId);
            if (waitingEntries.isEmpty()) {
                throw new IllegalStateException("No one waiting in queue");
            }
            
            QueueEntry nextEntry = waitingEntries.stream()
                .sorted(Comparator.comparing(QueueEntry::getQueueNumber))
                .findFirst()
                .orElse(waitingEntries.get(0));
            nextEntry.setStatus(QueueEntry.QueueStatus.CALLED);
            nextEntry.setCalledAt(LocalDateTime.now());
            
            QueueEntry savedEntry = queueEntryRepository.save(nextEntry);
            
            QueueUpdateDTO update = new QueueUpdateDTO(
                "CALLED", 
                enrichQueueEntryDTO(savedEntry), 
                "Queue number " + savedEntry.getQueueNumber() + " is being called",
                serviceId
            );
            messagingTemplate.convertAndSend("/topic/queue", update);
            
            return enrichQueueEntryDTO(savedEntry);
            
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
            
            QueueUpdateDTO update = new QueueUpdateDTO(
                "STARTED", 
                enrichQueueEntryDTO(savedEntry), 
                "Service started for queue number " + savedEntry.getQueueNumber(),
                savedEntry.getService().getId()
            );
            messagingTemplate.convertAndSend("/topic/queue", update);
            
            return enrichQueueEntryDTO(savedEntry);
            
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
                enrichQueueEntryDTO(savedEntry), 
                "Service completed for queue number " + savedEntry.getQueueNumber(),
                savedEntry.getService().getId()
            );
            messagingTemplate.convertAndSend("/topic/queue", update);
            
            return enrichQueueEntryDTO(savedEntry);
            
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
                .sorted(Comparator.comparing(QueueEntry::getQueueNumber))
                .map(this::enrichQueueEntryDTO)
                .collect(Collectors.toList());
    }
    
    public List<QueueEntryDTO> getUserQueueEntries(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        
        List<QueueEntry> entries = queueEntryRepository.findByUser(userOpt.get());
        return entries.stream()
                .sorted(Comparator.comparing(QueueEntry::getCreatedAt))
                .map(this::enrichQueueEntryDTO)
                .collect(Collectors.toList());
    }
    
    private Integer calculateEstimatedWaitTime(Long serviceId) {
        List<QueueEntry> waitingEntries = queueEntryRepository.findWaitingQueueEntriesByService(serviceId);
        Optional<com.easyq.common.model.Service> serviceOpt = serviceRepository.findById(serviceId);
        
        if (serviceOpt.isEmpty()) {
            return 30; 
        }
        
        int averageDuration = serviceOpt.get().getDurationMinutes();
        int peopleInQueue = waitingEntries.size();
        
        return averageDuration * peopleInQueue;
    }

    private int computePeopleAhead(QueueEntry entry) {
        if (entry == null || entry.getService() == null) {
            return 0;
        }
        List<QueueEntry> waitingEntries = queueEntryRepository.findWaitingQueueEntriesByService(entry.getService().getId());
        int queueNumber = Objects.requireNonNullElse(entry.getQueueNumber(), Integer.MAX_VALUE);
        long count = waitingEntries.stream()
                .filter(e -> e.getQueueNumber() != null && e.getQueueNumber() < queueNumber)
                .count();
        return (int) count;
    }

    private int computeAverageWaitTime(Long serviceId) {
        Optional<com.easyq.common.model.Service> serviceOpt = serviceRepository.findById(serviceId);
        if (serviceOpt.isEmpty() || serviceOpt.get().getDurationMinutes() == 0) {
            return 30;
        }
        return serviceOpt.get().getDurationMinutes();
    }

    private QueueEntryDTO enrichQueueEntryDTO(QueueEntry entry) {
        QueueEntryDTO dto = new QueueEntryDTO(entry);
        int peopleAhead = computePeopleAhead(entry);
        dto.setPeopleAhead(peopleAhead);
        int avg = computeAverageWaitTime(entry.getService().getId());
        dto.setAverageWaitTime(avg);
        dto.setEstimatedWaitTime(avg * peopleAhead);
        return dto;
    }

    private void validateIds(Long userId, Long serviceId) {
        if (serviceId == null || serviceId <= 0) {
            throw new IllegalArgumentException("Invalid service id");
        }
        if (userId != null && userId <= 0) {
            throw new IllegalArgumentException("Invalid user id");
        }
    }
}
