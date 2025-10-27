package com.easyq.booking.repository;

import com.easyq.common.model.Appointment;
import com.easyq.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByUser(User user);
    
    List<Appointment> findByUserAndStatus(User user, Appointment.AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date AND a.appointmentTime = :time")
    Appointment findByDateAndTime(@Param("date") LocalDate date, @Param("time") LocalTime time);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date ORDER BY a.appointmentTime")
    List<Appointment> findByDate(@Param("date") LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate >= :startDate AND a.appointmentDate <= :endDate ORDER BY a.appointmentDate, a.appointmentTime")
    List<Appointment> findAppointmentsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
