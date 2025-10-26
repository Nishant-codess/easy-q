package com.easyq.notification.controller;

import com.easyq.notification.dto.NotificationDTO;
import com.easyq.notification.service.NotificationService;
import com.easyq.common.model.Notification;
import com.easyq.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private com.easyq.admin.repository.UserRepository userRepository;
    
    @GetMapping("/")
    public String notificationsPage(Model model) {
        try {
            // Show ALL notifications (both sent and unsent) for demo
            List<NotificationDTO> unsentNotifications = notificationService.getUnsentNotifications();
            List<NotificationDTO> allNotifications = new ArrayList<>(unsentNotifications);
            
            // Add some sent notifications for demo (get recent notifications)
            List<NotificationDTO> recentNotifications = notificationService.getRecentNotifications(10);
            allNotifications.addAll(recentNotifications);
            
            // Sort by creation date (newest first)
            allNotifications.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            
            model.addAttribute("notifications", allNotifications);
        } catch (Exception e) {
            // If error, show empty list
            model.addAttribute("notifications", List.of());
            model.addAttribute("error", "No notifications available: " + e.getMessage());
        }
        return "notifications/notifications";
    }
    
    @GetMapping("/api/user-notifications")
    @ResponseBody
    public ResponseEntity<List<NotificationDTO>> getUserNotifications() {
        try {
            // For demo purposes, using user ID 4 (customer1)
            Long userId = 4L;
            List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    @GetMapping("/api/unsent")
    @ResponseBody
    public ResponseEntity<List<NotificationDTO>> getUnsentNotifications() {
        List<NotificationDTO> notifications = notificationService.getUnsentNotifications();
        return ResponseEntity.ok(notifications);
    }
    
    @PostMapping("/api/send-test")
    @ResponseBody
    public ResponseEntity<String> sendTestNotification(@RequestParam String type, @RequestParam String message) {
        try {
            // Try to find existing test user first
            String testEmail = "kg1409.pvt@gmail.com";
            User testUser = userRepository.findByEmail(testEmail).orElse(null);
            
            if (testUser == null) {
                // Create a new test user only if one doesn't exist
                testUser = new User();
                testUser.setEmail(testEmail);
                testUser.setPhone("+918436964391");
                testUser.setFirstName("Test");
                testUser.setLastName("User");
                testUser.setUsername("testuser" + System.currentTimeMillis());
                testUser.setPassword("password");
                testUser.setRole(User.Role.CUSTOMER);
                testUser.setIsActive(true);
                
                // Save user first
                testUser = userRepository.save(testUser);
            }
            
            Notification.NotificationType ntype = Notification.NotificationType.valueOf(type);
            Notification notification = notificationService.createNotification(testUser, ntype, "TEST-" + ntype.name(), message);
            notificationService.sendNotification(notification);
            return ResponseEntity.ok("Test notification dispatched successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send test notification: " + e.getMessage());
        }
    }
}
