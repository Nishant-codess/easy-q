package com.easyq.notification.scheduler;

import com.easyq.notification.repository.NotificationRepository;
import com.easyq.notification.service.NotificationService;
import com.easyq.common.model.Notification;
import com.easyq.common.model.Appointment;
import com.easyq.admin.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Value("${notification.appointment.reminder-hours:24}")
    private int reminderHours;
    
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
            
            // Find appointments scheduled within the reminder window
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reminderWindowStart = now.plusHours(reminderHours - 1);
            LocalDateTime reminderWindowEnd = now.plusHours(reminderHours + 1);
            
            // Get all appointments and filter (simplified approach)
            List<Appointment> allAppointments = appointmentRepository.findAll();
            List<Appointment> upcomingAppointments = allAppointments.stream()
                .filter(apt -> apt.getAppointmentDate() != null)
                .filter(apt -> apt.getAppointmentDate().isAfter(reminderWindowStart.toLocalDate().minusDays(1)))
                .filter(apt -> apt.getAppointmentDate().isBefore(reminderWindowEnd.toLocalDate().plusDays(1)))
                .filter(apt -> apt.getStatus() == Appointment.AppointmentStatus.SCHEDULED)
                .collect(java.util.stream.Collectors.toList());
            
            System.out.println("Found " + upcomingAppointments.size() + " appointments in reminder window");
            
            for (Appointment appointment : upcomingAppointments) {
                try {
                    // Check if reminder already sent (simple check - could be enhanced)
                    boolean reminderSent = notificationRepository
                        .findByUserAndTypeAndCreatedAtAfter(
                            appointment.getUser(),
                            Notification.NotificationType.APPOINTMENT_REMINDER,
                            now.minusHours(1)
                        ).size() > 0;
                    
                    if (!reminderSent) {
                        notificationService.sendAppointmentReminder(appointment);
                        System.out.println("Sent reminder for appointment ID: " + appointment.getId());
                    }
                } catch (Exception e) {
                    System.err.println("Failed to send reminder for appointment " + appointment.getId() + ": " + e.getMessage());
                }
            }
            
            System.out.println("Appointment reminder check completed");
            
        } catch (Exception e) {
            System.err.println("Error in appointment reminder scheduler: " + e.getMessage());
        }
    }
    
    // Run every 5 minutes to check for queue updates and waiting time reminders
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void checkQueueUpdates() {
        try {
            System.out.println("Checking for queue updates at " + LocalDateTime.now());
            
            // Check for queue entries with 5-minute waiting time
            checkWaitingTimeReminders();
            
            System.out.println("Queue update check completed");
            
        } catch (Exception e) {
            System.err.println("Error in queue update scheduler: " + e.getMessage());
        }
    }
    
    private void checkWaitingTimeReminders() {
        try {
            // Get all active queue entries
            List<com.easyq.common.model.QueueEntry> activeQueues = notificationService.getActiveQueueEntries();
            
            for (com.easyq.common.model.QueueEntry queueEntry : activeQueues) {
                // Check if estimated wait time is 5 minutes or less
                if (queueEntry.getEstimatedWaitTime() != null && queueEntry.getEstimatedWaitTime() <= 5) {
                    // Check if we haven't already sent a reminder in the last 10 minutes
                    boolean reminderSent = notificationService.hasRecentReminder(
                        queueEntry.getUser(), 
                        com.easyq.common.model.Notification.NotificationType.QUEUE_UPDATE,
                        LocalDateTime.now().minusMinutes(10)
                    );
                    
                    if (!reminderSent) {
                        // Send waiting time reminder
                        notificationService.sendQueueUpdate(queueEntry);
                        System.out.println("Sent waiting time reminder for queue entry: " + queueEntry.getId());
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error checking waiting time reminders: " + e.getMessage());
        }
    }
}
