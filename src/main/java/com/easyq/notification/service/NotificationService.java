package com.easyq.notification.service;

import com.easyq.notification.dto.NotificationDTO;
import com.easyq.notification.repository.NotificationRepository;
import com.easyq.common.model.Notification;
import com.easyq.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// SMS imports removed - using Gmail only

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private com.easyq.admin.repository.UserRepository userRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;
    
    // TODO: Configure these properties in application.properties
    // SMS configuration removed - using Gmail only
    
    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;
    
    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;
    
    @Value("${notification.retry.max-attempts:3}")
    private int maxRetryAttempts;
    
    @Value("${notification.retry.delay-minutes:5}")
    private int retryDelayMinutes;
    
    @Value("${notification.smtp.username:}")
    private String smtpUsername;
    
    public Notification createNotification(User user, Notification.NotificationType type, String title, String message) {
        Notification notification = new Notification(user, type, title, message);
        return notificationRepository.save(notification);
    }
    
    @Async
    public void sendNotification(Notification notification) {
        try {
            // Fetch the user with all required fields to avoid LazyInitializationException
            User user = userRepository.findById(notification.getUser().getId()).orElse(null);
            if (user == null) {
                System.err.println("[Notification] User not found for notification ID: " + notification.getId());
                return;
            }
            
            boolean emailSent = false;
            boolean smsSent = false;
            
            // Send via configured channels
            if (emailEnabled && user.getEmail() != null) {
                emailSent = sendEmail(notification, user);
            }
            if (smsEnabled && user.getPhone() != null) {
                smsSent = sendSMS(notification, user);
            }
            
            if (!emailEnabled && !smsEnabled) {
                System.out.println("[Notification] No channels enabled; skipping send. Title=" + notification.getTitle());
            }
            
            // Mark as sent if at least one channel succeeded
            if (emailSent || smsSent || (!emailEnabled && !smsEnabled)) {
                notification.setIsSent(true);
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
                System.out.println("[Notification] Sent successfully. Email=" + emailSent + ", SMS=" + smsSent);
                
                // Broadcast via WebSocket if available
                if (messagingTemplate != null) {
                    try {
                        NotificationDTO notificationDTO = new NotificationDTO(notification);
                        messagingTemplate.convertAndSend("/topic/notifications", notificationDTO);
                        System.out.println("[WebSocket] Broadcasted notification: " + notification.getId());
                    } catch (Exception e) {
                        System.err.println("[WebSocket] Failed to broadcast notification: " + e.getMessage());
                    }
                }
            } else {
                System.err.println("[Notification] Failed to send via any channel. ID=" + notification.getId());
            }
            
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void sendAppointmentReminder(com.easyq.common.model.Appointment appointment) {
        User user = appointment.getUser();
        String title = "Appointment Reminder";
        String message = String.format("Your appointment for %s is scheduled for %s at %s",
            appointment.getService().getName(),
            appointment.getAppointmentDate(),
            appointment.getAppointmentTime());
        
        Notification notification = createNotification(user, Notification.NotificationType.APPOINTMENT_REMINDER, title, message);
        sendNotification(notification);
    }
    
    public void sendQueueUpdate(com.easyq.common.model.QueueEntry queueEntry) {
        User user = queueEntry.getUser();
        String title = "Queue Update";
        String message = String.format("Your queue number is %d. Estimated wait time: %d minutes",
            queueEntry.getQueueNumber(),
            queueEntry.getEstimatedWaitTime());
        
        Notification notification = createNotification(user, Notification.NotificationType.QUEUE_UPDATE, title, message);
        sendNotification(notification);
    }
    
    public void sendQueueCalled(com.easyq.common.model.QueueEntry queueEntry) {
        User user = queueEntry.getUser();
        String title = "You are being called";
        String message = String.format("Please proceed to the service counter. Queue number: %d",
            queueEntry.getQueueNumber());
        
        Notification notification = createNotification(user, Notification.NotificationType.QUEUE_CALLED, title, message);
        sendNotification(notification);
    }
    
    public void sendAppointmentConfirmation(com.easyq.common.model.Appointment appointment) {
        User user = appointment.getUser();
        String title = "Appointment Confirmed";
        String message = String.format("Your appointment for %s has been confirmed for %s at %s",
            appointment.getService().getName(),
            appointment.getAppointmentDate(),
            appointment.getAppointmentTime());
        
        Notification notification = createNotification(user, Notification.NotificationType.APPOINTMENT_CONFIRMATION, title, message);
        sendNotification(notification);
    }
    
    public List<NotificationDTO> getUserNotifications(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                System.out.println("User with ID " + userId + " not found");
                return List.of();
            }
            
            List<Notification> notifications = notificationRepository.findByUser(userOpt.get());
            return notifications.stream()
                    .map(NotificationDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting user notifications: " + e.getMessage());
            return List.of();
        }
    }
    
    public List<NotificationDTO> getUnsentNotifications() {
        List<Notification> notifications = notificationRepository.findByIsSent(false);
        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }
    
    public List<com.easyq.common.model.QueueEntry> getActiveQueueEntries() {
        // This would typically query the queue repository
        // For now, return empty list as placeholder
        return List.of();
    }
    
    public boolean hasRecentReminder(User user, Notification.NotificationType type, LocalDateTime since) {
        List<Notification> recentNotifications = notificationRepository
            .findByUserAndTypeAndCreatedAtAfter(user, type, since);
        return !recentNotifications.isEmpty();
    }
    
    private boolean sendSMS(Notification notification, User user) {
        // SMS disabled - using Gmail only
        System.out.println("[SMS] SMS disabled; using Gmail only.");
        return false;
    }
    
    private boolean sendEmail(Notification notification, User user) {
        try {
            if (!emailEnabled) {
                System.out.println("[Email] Email disabled; skipping.");
                return false;
            }
            
            if (mailSender == null) {
                System.err.println("[Email] JavaMailSender not configured; cannot send email.");
                return false;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setFrom(smtpUsername != null ? smtpUsername : "no-reply@easyq.local");
            message.setSubject(notification.getTitle());
            message.setText(notification.getMessage());
            
            mailSender.send(message);
            System.out.println("[Email] Sent to=" + user.getEmail());
            return true;
            
        } catch (Exception ex) {
            System.err.println("[Email] Failed to send to " + user.getEmail() + 
                             ": " + ex.getMessage());
            return false;
        }
    }
    
    public List<NotificationDTO> getRecentNotifications(int limit) {
        return notificationRepository.findAll()
            .stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .limit(limit)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private NotificationDTO convertToDTO(Notification notification) {
        return new NotificationDTO(notification);
    }
}
