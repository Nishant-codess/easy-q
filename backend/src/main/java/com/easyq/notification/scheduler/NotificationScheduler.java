package com.easyq.notification.scheduler;

import com.easyq.notification.repository.NotificationRepository;
import com.easyq.notification.service.NotificationService;
import com.easyq.common.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationScheduler {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    // Run every 5 minutes to process unsent notifications
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void processUnsentNotifications() {
        try {
            System.out.println("Processing unsent notifications at " + LocalDateTime.now());
            
            // Get notifications that are older than 1 minute and haven't been sent
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(1);
            List<Notification> unsentNotifications = notificationRepository.findUnsentNotificationsBefore(cutoffTime);
            
            System.out.println("Found " + unsentNotifications.size() + " unsent notifications");
            
            for (Notification notification : unsentNotifications) {
                try {
                    notificationService.sendNotification(notification);
                    System.out.println("Processed notification ID: " + notification.getId());
                } catch (Exception e) {
                    System.err.println("Failed to process notification ID " + notification.getId() + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error in notification scheduler: " + e.getMessage());
        }
    }
    
    // Run every hour to send appointment reminders
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void sendAppointmentReminders() {
        try {
            System.out.println("Checking for appointment reminders at " + LocalDateTime.now());
            
            // TODO: Implement appointment reminder logic
            // This would typically:
            // 1. Find appointments scheduled for the next 24 hours
            // 2. Check if reminders have already been sent
            // 3. Send reminders for appointments that need them
            
            System.out.println("Appointment reminder check completed");
            
        } catch (Exception e) {
            System.err.println("Error in appointment reminder scheduler: " + e.getMessage());
        }
    }
    
    // Run every 10 minutes to check for queue updates
    @Scheduled(fixedRate = 600000) // 10 minutes in milliseconds
    public void checkQueueUpdates() {
        try {
            System.out.println("Checking for queue updates at " + LocalDateTime.now());
            
            // TODO: Implement queue update logic
            // This would typically:
            // 1. Check for queue entries that have been waiting for a while
            // 2. Update estimated wait times
            // 3. Send notifications if wait times have changed significantly
            
            System.out.println("Queue update check completed");
            
        } catch (Exception e) {
            System.err.println("Error in queue update scheduler: " + e.getMessage());
        }
    }
}
