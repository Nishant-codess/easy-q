package com.easyq.notification.controller;

import com.easyq.notification.dto.NotificationDTO;
import com.easyq.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class NotificationWebSocketController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/notifications/subscribe")
    @SendTo("/topic/notifications")
    public List<NotificationDTO> subscribeToNotifications() {
        // Return unsent notifications when someone subscribes
        return notificationService.getUnsentNotifications();
    }
    
    public void broadcastNotification(NotificationDTO notification) {
        // Broadcast notification to all connected clients
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
    
    public void broadcastNotificationList(List<NotificationDTO> notifications) {
        // Broadcast notification list to all connected clients
        messagingTemplate.convertAndSend("/topic/notifications", notifications);
    }
}

