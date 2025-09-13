package com.easyq.admin.repository;

import com.easyq.common.model.AnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, Long> {
    
    List<AnalyticsEvent> findByEventType(String eventType);
    
    @Query("SELECT ae FROM AnalyticsEvent ae WHERE ae.createdAt >= :startDate AND ae.createdAt <= :endDate")
    List<AnalyticsEvent> findEventsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ae.eventType, COUNT(ae) FROM AnalyticsEvent ae WHERE ae.createdAt >= :startDate AND ae.createdAt <= :endDate GROUP BY ae.eventType")
    List<Object[]> countEventsByTypeBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(ae) FROM AnalyticsEvent ae WHERE ae.eventType = :eventType AND ae.createdAt >= :startDate AND ae.createdAt <= :endDate")
    Long countEventsByTypeBetweenDates(@Param("eventType") String eventType, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
