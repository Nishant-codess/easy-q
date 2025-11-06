package com.easyq.admin.service;

import com.easyq.admin.dto.DashboardStatsDTO;
import com.easyq.admin.repository.*;
import com.easyq.common.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private QueueEntryRepository queueEntryRepository;
    
    @Autowired
    private AnalyticsEventRepository analyticsEventRepository;
    
    public DashboardStatsDTO getDashboardStats() {
        // Basic counts
        Long totalUsers = userRepository.count();
        Long totalAppointments = appointmentRepository.count();
        Long totalQueueEntries = queueEntryRepository.count();
        Long activeServices = serviceRepository.countActiveServices();
        
        // Today's stats
        LocalDate today = LocalDate.now();
        Long todayAppointments = appointmentRepository.countAppointmentsCreatedBetween(
            today.atStartOfDay(), today.plusDays(1).atStartOfDay());
        
        Long waitingInQueue = queueEntryRepository.countByStatus(QueueEntry.QueueStatus.WAITING);
        Long completedToday = appointmentRepository.countByStatus(Appointment.AppointmentStatus.COMPLETED);
        Long cancelledToday = appointmentRepository.countByStatus(Appointment.AppointmentStatus.CANCELLED);
        
        DashboardStatsDTO stats = new DashboardStatsDTO(
            totalUsers, totalAppointments, totalQueueEntries, activeServices,
            todayAppointments, waitingInQueue, completedToday, cancelledToday
        );
        
        // Chart data for last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime now = LocalDateTime.now();
        
        // Appointments by service
        List<Object[]> appointmentsByServiceData = appointmentRepository.countAppointmentsByServiceBetweenDates(thirtyDaysAgo, now);
        List<Map<String, Object>> appointmentsByService = new ArrayList<>();
        for (Object[] row : appointmentsByServiceData) {
            Map<String, Object> data = new HashMap<>();
            data.put("service", row[0]);
            data.put("count", row[1]);
            appointmentsByService.add(data);
        }
        stats.setAppointmentsByService(appointmentsByService);
        
        // Queue entries by service
        List<Object[]> queueEntriesByServiceData = queueEntryRepository.countQueueEntriesByServiceBetweenDates(thirtyDaysAgo, now);
        List<Map<String, Object>> queueEntriesByService = new ArrayList<>();
        for (Object[] row : queueEntriesByServiceData) {
            Map<String, Object> data = new HashMap<>();
            data.put("service", row[0]);
            data.put("count", row[1]);
            queueEntriesByService.add(data);
        }
        stats.setQueueEntriesByService(queueEntriesByService);
        
        // Daily appointments for last 7 days
        List<Map<String, Object>> dailyAppointments = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Long count = appointmentRepository.countAppointmentsCreatedBetween(
                date.atStartOfDay(), date.plusDays(1).atStartOfDay());
            Map<String, Object> data = new HashMap<>();
            data.put("date", date.format(DateTimeFormatter.ofPattern("MMM dd")));
            data.put("count", count);
            dailyAppointments.add(data);
        }
        stats.setDailyAppointments(dailyAppointments);
        
        // User registrations for last 7 days
        List<Map<String, Object>> userRegistrations = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Long count = userRepository.countUsersCreatedBetween(
                date.atStartOfDay(), date.plusDays(1).atStartOfDay());
            Map<String, Object> data = new HashMap<>();
            data.put("date", date.format(DateTimeFormatter.ofPattern("MMM dd")));
            data.put("count", count);
            userRegistrations.add(data);
        }
        stats.setUserRegistrations(userRegistrations);
        
        // Recent appointments
        List<Appointment> recentAppointmentsList = appointmentRepository.findAppointmentsBetweenDates(
            today.minusDays(7), today.plusDays(7));
        List<Map<String, Object>> recentAppointments = new ArrayList<>();
        for (Appointment appointment : recentAppointmentsList.stream().limit(10).toList()) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", appointment.getId());
            data.put("userName", appointment.getUser().getFirstName() + " " + appointment.getUser().getLastName());
            data.put("serviceName", appointment.getService().getName());
            data.put("date", appointment.getAppointmentDate());
            data.put("time", appointment.getAppointmentTime());
            data.put("status", appointment.getStatus());
            recentAppointments.add(data);
        }
        stats.setRecentAppointments(recentAppointments);
        
        // Recent queue entries
        List<QueueEntry> recentQueueEntriesList = queueEntryRepository.findWaitingQueueEntries();
        List<Map<String, Object>> recentQueueEntries = new ArrayList<>();
        for (QueueEntry queueEntry : recentQueueEntriesList.stream().limit(10).toList()) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", queueEntry.getId());
            data.put("userName", queueEntry.getUser().getFirstName() + " " + queueEntry.getUser().getLastName());
            data.put("serviceName", queueEntry.getService().getName());
            data.put("queueNumber", queueEntry.getQueueNumber());
            data.put("status", queueEntry.getStatus());
            data.put("estimatedWaitTime", queueEntry.getEstimatedWaitTime());
            recentQueueEntries.add(data);
        }
        stats.setRecentQueueEntries(recentQueueEntries);
        
        return stats;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<com.easyq.common.model.Service> getAllServices() {
        return serviceRepository.findAll();
    }
    
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    
    public List<Appointment> getDentalAppointments() {
        return appointmentRepository.findByService_Name("Dental Checkup");
    }
    
    public List<QueueEntry> getAllQueueEntries() {
        return queueEntryRepository.findAll();
    }
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    public com.easyq.common.model.Service createService(com.easyq.common.model.Service service) {
        return serviceRepository.save(service);
    }
    
    public com.easyq.common.model.Service updateService(com.easyq.common.model.Service service) {
        return serviceRepository.save(service);
    }
    
    public void deleteService(Long serviceId) {
        serviceRepository.deleteById(serviceId);
    }
    
    public void logAnalyticsEvent(String eventType, String eventData) {
        AnalyticsEvent event = new AnalyticsEvent(eventType, eventData);
        analyticsEventRepository.save(event);
    }
}
