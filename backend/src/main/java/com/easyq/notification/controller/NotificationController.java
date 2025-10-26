package com.easyq.notification.controller;

import com.easyq.notification.dto.NotificationDTO;
import com.easyq.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping
    public String notificationsPage(Model model) {
        // For demo purposes, using user ID 4 (customer1)
        Long userId = 4L;
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
        model.addAttribute("notifications", notifications);
        return "notifications/notifications";
    }
    
    @GetMapping("/api/user-notifications")
    @ResponseBody
    public ResponseEntity<List<NotificationDTO>> getUserNotifications() {
        // For demo purposes, using user ID 4 (customer1)
        Long userId = 4L;
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
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
            // For demo purposes, using user ID 4 (customer1)
            Long userId = 4L;
            
            // TODO: Implement test notification sending
            System.out.println("Test notification: " + type + " - " + message);
            
            return ResponseEntity.ok("Test notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send test notification: " + e.getMessage());
        }
    }
}
