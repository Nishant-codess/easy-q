package com.easyq.notification.repository;

import com.easyq.common.model.Notification;
import com.easyq.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUser(User user);
    
    List<Notification> findByIsSent(Boolean isSent);
    
    List<Notification> findByType(Notification.NotificationType type);
    
    @Query("SELECT n FROM Notification n WHERE n.isSent = false AND n.createdAt <= :cutoffTime")
    List<Notification> findUnsentNotificationsBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isSent = false ORDER BY n.createdAt DESC")
    List<Notification> findUnsentNotificationsByUser(@Param("user") User user);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.type = :type AND n.createdAt > :since")
    List<Notification> findByUserAndTypeAndCreatedAtAfter(@Param("user") User user, 
                                                         @Param("type") Notification.NotificationType type, 
                                                         @Param("since") LocalDateTime since);
}
