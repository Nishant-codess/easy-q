package com.easyq.notification.service;

import com.easyq.notification.dto.NotificationDTO;
import com.easyq.notification.repository.NotificationRepository;
import com.easyq.common.model.Notification;
import com.easyq.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    
    // TODO: Configure these properties in application.properties
    @Value("${notification.twilio.account-sid:}")
    private String twilioAccountSid;
    
    @Value("${notification.twilio.auth-token:}")
    private String twilioAuthToken;
    
    @Value("${notification.twilio.phone-number:}")
    private String twilioPhoneNumber;
    
    @Value("${notification.smtp.host:}")
    private String smtpHost;
    
    @Value("${notification.smtp.username:}")
    private String smtpUsername;
    
    @Value("${notification.smtp.password:}")
    private String smtpPassword;
    
    public Notification createNotification(User user, Notification.NotificationType type, String title, String message) {
        Notification notification = new Notification(user, type, title, message);
        return notificationRepository.save(notification);
    }
    
    @Async
    public void sendNotification(Notification notification) {
        try {
            // TODO: Implement actual notification sending logic
            // For now, just log the notification
            System.out.println("Sending notification to " + notification.getUser().getEmail() + 
                             ": " + notification.getTitle() + " - " + notification.getMessage());
            
            // Mark as sent
            notification.setIsSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            // TODO: Implement Twilio SMS sending
            // if (notification.getType() == Notification.NotificationType.QUEUE_CALLED) {
            //     sendSMS(notification);
            // }
            
            // TODO: Implement SMTP email sending
            // if (notification.getType() == Notification.NotificationType.APPOINTMENT_REMINDER) {
            //     sendEmail(notification);
            // }
            
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
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
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        
        List<Notification> notifications = notificationRepository.findByUser(userOpt.get());
        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }
    
    public List<NotificationDTO> getUnsentNotifications() {
        List<Notification> notifications = notificationRepository.findByIsSent(false);
        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }
    
    // TODO: Implement actual SMS sending with Twilio
    private void sendSMS(Notification notification) {
        // Twilio SMS implementation would go here
        System.out.println("SMS would be sent to " + notification.getUser().getPhone() + 
                         ": " + notification.getMessage());
    }
    
    // TODO: Implement actual email sending with SMTP
    private void sendEmail(Notification notification) {
        // SMTP email implementation would go here
        System.out.println("Email would be sent to " + notification.getUser().getEmail() + 
                         ": " + notification.getTitle() + " - " + notification.getMessage());
    }
}
