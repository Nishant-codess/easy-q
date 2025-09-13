package com.easyq.queue.repository;

import com.easyq.common.model.QueueEntry;
import com.easyq.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {
    
    List<QueueEntry> findByUser(User user);
    
    List<QueueEntry> findByStatus(QueueEntry.QueueStatus status);
    
    @Query("SELECT q FROM QueueEntry q WHERE q.status = 'WAITING' ORDER BY q.queueNumber ASC")
    List<QueueEntry> findWaitingQueueEntries();
    
    @Query("SELECT q FROM QueueEntry q WHERE q.service.id = :serviceId AND q.status = 'WAITING' ORDER BY q.queueNumber ASC")
    List<QueueEntry> findWaitingQueueEntriesByService(@Param("serviceId") Long serviceId);
    
    @Query("SELECT MAX(q.queueNumber) FROM QueueEntry q WHERE q.service.id = :serviceId")
    Optional<Integer> findMaxQueueNumberByService(@Param("serviceId") Long serviceId);
    
    @Query("SELECT q FROM QueueEntry q WHERE q.user = :user AND q.status IN ('WAITING', 'CALLED', 'IN_PROGRESS')")
    List<QueueEntry> findActiveQueueEntriesByUser(@Param("user") User user);
}
