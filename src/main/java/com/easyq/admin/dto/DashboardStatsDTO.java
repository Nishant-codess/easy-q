package com.easyq.admin.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DashboardStatsDTO {
    
    private Long totalUsers;
    private Long totalAppointments;
    private Long totalQueueEntries;
    private Long activeServices;
    private Long todayAppointments;
    private Long waitingInQueue;
    private Long completedToday;
    private Long cancelledToday;
    
    // Chart data
    private List<Map<String, Object>> appointmentsByService;
    private List<Map<String, Object>> queueEntriesByService;
    private List<Map<String, Object>> dailyAppointments;
    private List<Map<String, Object>> userRegistrations;
    
    // Recent activity
    private List<Map<String, Object>> recentAppointments;
    private List<Map<String, Object>> recentQueueEntries;
    
    private LocalDateTime lastUpdated;
    
    // Constructors
    public DashboardStatsDTO() {}
    
    public DashboardStatsDTO(Long totalUsers, Long totalAppointments, Long totalQueueEntries, 
                           Long activeServices, Long todayAppointments, Long waitingInQueue,
                           Long completedToday, Long cancelledToday) {
        this.totalUsers = totalUsers;
        this.totalAppointments = totalAppointments;
        this.totalQueueEntries = totalQueueEntries;
        this.activeServices = activeServices;
        this.todayAppointments = todayAppointments;
        this.waitingInQueue = waitingInQueue;
        this.completedToday = completedToday;
        this.cancelledToday = cancelledToday;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers;
    }
    
    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public Long getTotalAppointments() {
        return totalAppointments;
    }
    
    public void setTotalAppointments(Long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }
    
    public Long getTotalQueueEntries() {
        return totalQueueEntries;
    }
    
    public void setTotalQueueEntries(Long totalQueueEntries) {
        this.totalQueueEntries = totalQueueEntries;
    }
    
    public Long getActiveServices() {
        return activeServices;
    }
    
    public void setActiveServices(Long activeServices) {
        this.activeServices = activeServices;
    }
    
    public Long getTodayAppointments() {
        return todayAppointments;
    }
    
    public void setTodayAppointments(Long todayAppointments) {
        this.todayAppointments = todayAppointments;
    }
    
    public Long getWaitingInQueue() {
        return waitingInQueue;
    }
    
    public void setWaitingInQueue(Long waitingInQueue) {
        this.waitingInQueue = waitingInQueue;
    }
    
    public Long getCompletedToday() {
        return completedToday;
    }
    
    public void setCompletedToday(Long completedToday) {
        this.completedToday = completedToday;
    }
    
    public Long getCancelledToday() {
        return cancelledToday;
    }
    
    public void setCancelledToday(Long cancelledToday) {
        this.cancelledToday = cancelledToday;
    }
    
    public List<Map<String, Object>> getAppointmentsByService() {
        return appointmentsByService;
    }
    
    public void setAppointmentsByService(List<Map<String, Object>> appointmentsByService) {
        this.appointmentsByService = appointmentsByService;
    }
    
    public List<Map<String, Object>> getQueueEntriesByService() {
        return queueEntriesByService;
    }
    
    public void setQueueEntriesByService(List<Map<String, Object>> queueEntriesByService) {
        this.queueEntriesByService = queueEntriesByService;
    }
    
    public List<Map<String, Object>> getDailyAppointments() {
        return dailyAppointments;
    }
    
    public void setDailyAppointments(List<Map<String, Object>> dailyAppointments) {
        this.dailyAppointments = dailyAppointments;
    }
    
    public List<Map<String, Object>> getUserRegistrations() {
        return userRegistrations;
    }
    
    public void setUserRegistrations(List<Map<String, Object>> userRegistrations) {
        this.userRegistrations = userRegistrations;
    }
    
    public List<Map<String, Object>> getRecentAppointments() {
        return recentAppointments;
    }
    
    public void setRecentAppointments(List<Map<String, Object>> recentAppointments) {
        this.recentAppointments = recentAppointments;
    }
    
    public List<Map<String, Object>> getRecentQueueEntries() {
        return recentQueueEntries;
    }
    
    public void setRecentQueueEntries(List<Map<String, Object>> recentQueueEntries) {
        this.recentQueueEntries = recentQueueEntries;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
