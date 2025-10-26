package com.easyq.admin.repository;

import com.easyq.common.model.QueueEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.easyq.common.model.User;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {
    
    List<QueueEntry> findByStatus(QueueEntry.QueueStatus status);
    
    @Query("SELECT q FROM QueueEntry q WHERE q.status = 'WAITING' ORDER BY q.queueNumber ASC")
    List<QueueEntry> findWaitingQueueEntries();
    
    @Query("SELECT COUNT(q) FROM QueueEntry q WHERE q.status = :status")
    Long countByStatus(@Param("status") QueueEntry.QueueStatus status);
    
    @Query("SELECT COUNT(q) FROM QueueEntry q WHERE q.createdAt >= :startDate AND q.createdAt <= :endDate")
    Long countQueueEntriesCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT q.service.name, COUNT(q) FROM QueueEntry q WHERE q.createdAt >= :startDate AND q.createdAt <= :endDate GROUP BY q.service.name")
    List<Object[]> countQueueEntriesByServiceBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Additional methods needed by QueueService
    @Query("SELECT q FROM QueueEntry q WHERE q.user = :user AND q.status IN ('WAITING', 'CALLED', 'IN_PROGRESS')")
    List<QueueEntry> findActiveQueueEntriesByUser(@Param("user") User user);
    
    @Query("SELECT MAX(q.queueNumber) FROM QueueEntry q WHERE q.service.id = :serviceId")
    Optional<Integer> findMaxQueueNumberByService(@Param("serviceId") Long serviceId);
    
    @Query("SELECT q FROM QueueEntry q WHERE q.service.id = :serviceId AND q.status = 'WAITING' ORDER BY q.queueNumber ASC")
    List<QueueEntry> findWaitingQueueEntriesByService(@Param("serviceId") Long serviceId);
    
    List<QueueEntry> findByUser(User user);
}
