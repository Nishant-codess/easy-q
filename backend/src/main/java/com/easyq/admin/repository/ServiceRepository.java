package com.easyq.admin.repository;

import com.easyq.common.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    List<Service> findByIsActive(Boolean isActive);
    
    @Query("SELECT COUNT(s) FROM Service s WHERE s.isActive = true")
    Long countActiveServices();
}
