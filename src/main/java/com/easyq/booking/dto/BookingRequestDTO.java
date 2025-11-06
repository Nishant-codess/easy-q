package com.easyq.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingRequestDTO {
    
    @NotNull(message = "Service ID is required")
    private Long serviceId;
    
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDate appointmentDate;
    
    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;
    
    private String notes;
    
    private String patientName;
    
    // Constructors
    public BookingRequestDTO() {}
    
    public BookingRequestDTO(Long serviceId, LocalDate appointmentDate, LocalTime appointmentTime, String notes) {
        this.serviceId = serviceId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.notes = notes;
    }
    
    // Getters and Setters
    public Long getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
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
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}
