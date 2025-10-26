package com.easyq.booking.dto;

import com.easyq.common.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingResponseDTO {
    
    private Long id;
    private String serviceName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Appointment.AppointmentStatus status;
    private String notes;
    private String message;
    private boolean success;
    
    // Constructors
    public BookingResponseDTO() {}
    
    public BookingResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public BookingResponseDTO(Appointment appointment) {
        this.id = appointment.getId();
        this.serviceName = appointment.getService().getName();
        this.appointmentDate = appointment.getAppointmentDate();
        this.appointmentTime = appointment.getAppointmentTime();
        this.status = appointment.getStatus();
        this.notes = appointment.getNotes();
        this.success = true;
        this.message = "Appointment booked successfully";
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }
    
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }
    
    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    public Appointment.AppointmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(Appointment.AppointmentStatus status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
