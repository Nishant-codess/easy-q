package com.easyq.admin.repository;

import com.easyq.common.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import com.easyq.common.model.User;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
    
    List<Appointment> findByAppointmentDate(LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate >= :startDate AND a.appointmentDate <= :endDate")
    List<Appointment> findAppointmentsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    Long countByStatus(@Param("status") Appointment.AppointmentStatus status);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.createdAt >= :startDate AND a.createdAt <= :endDate")
    Long countAppointmentsCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a.service.name, COUNT(a) FROM Appointment a WHERE a.createdAt >= :startDate AND a.createdAt <= :endDate GROUP BY a.service.name")
    List<Object[]> countAppointmentsByServiceBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Additional methods needed by BookingService
    Optional<Appointment> findByAppointmentDateAndAppointmentTime(LocalDate appointmentDate, LocalTime appointmentTime);

    List<Appointment> findByUser(User user);
}
