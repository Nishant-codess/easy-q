package com.easyq.booking.service;

import com.easyq.booking.dto.BookingRequestDTO;
import com.easyq.booking.dto.BookingResponseDTO;
import com.easyq.booking.repository.AppointmentRepository;
import com.easyq.common.model.Appointment;
import com.easyq.common.model.Service;
import com.easyq.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private com.easyq.admin.repository.ServiceRepository serviceRepository;
    
    @Autowired
    private com.easyq.admin.repository.UserRepository userRepository;
    
    public BookingResponseDTO bookAppointment(BookingRequestDTO request, Long userId) {
        try {
            // Validate service exists
            Optional<Service> serviceOpt = serviceRepository.findById(request.getServiceId());
            if (serviceOpt.isEmpty()) {
                return new BookingResponseDTO(false, "Service not found");
            }
            
            // Validate user exists
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return new BookingResponseDTO(false, "User not found");
            }
            
            // Check if time slot is available
            Appointment existingAppointment = appointmentRepository.findByDateAndTime(
                request.getAppointmentDate(), request.getAppointmentTime());
            if (existingAppointment != null) {
                return new BookingResponseDTO(false, "Time slot is already booked");
            }
            
            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setUser(userOpt.get());
            appointment.setService(serviceOpt.get());
            appointment.setAppointmentDate(request.getAppointmentDate());
            appointment.setAppointmentTime(request.getAppointmentTime());
            appointment.setNotes(request.getNotes());
            appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
            
            Appointment savedAppointment = appointmentRepository.save(appointment);
            
            // TODO: Send confirmation notification
            // notificationService.sendAppointmentConfirmation(savedAppointment);
            
            return new BookingResponseDTO(savedAppointment);
            
        } catch (Exception e) {
            return new BookingResponseDTO(false, "Failed to book appointment: " + e.getMessage());
        }
    }
    
    public List<Appointment> getUserAppointments(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        return appointmentRepository.findByUser(userOpt.get());
    }
    
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByDate(date);
    }
    
    public List<Appointment> getAppointmentsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findAppointmentsBetweenDates(startDate, endDate);
    }
    
    public BookingResponseDTO cancelAppointment(Long appointmentId, Long userId) {
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                return new BookingResponseDTO(false, "Appointment not found");
            }
            
            Appointment appointment = appointmentOpt.get();
            if (!appointment.getUser().getId().equals(userId)) {
                return new BookingResponseDTO(false, "Unauthorized to cancel this appointment");
            }
            
            if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
                return new BookingResponseDTO(false, "Cannot cancel completed appointment");
            }
            
            appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
            appointmentRepository.save(appointment);
            
            // TODO: Send cancellation notification
            // notificationService.sendAppointmentCancellation(appointment);
            
            return new BookingResponseDTO(true, "Appointment cancelled successfully");
            
        } catch (Exception e) {
            return new BookingResponseDTO(false, "Failed to cancel appointment: " + e.getMessage());
        }
    }
    
    public BookingResponseDTO updateAppointment(Long appointmentId, BookingRequestDTO request, Long userId) {
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                return new BookingResponseDTO(false, "Appointment not found");
            }
            
            Appointment appointment = appointmentOpt.get();
            if (!appointment.getUser().getId().equals(userId)) {
                return new BookingResponseDTO(false, "Unauthorized to update this appointment");
            }
            
            if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
                return new BookingResponseDTO(false, "Cannot update completed appointment");
            }
            
            // Check if new time slot is available
            Appointment existingAppointment = appointmentRepository.findByDateAndTime(
                request.getAppointmentDate(), request.getAppointmentTime());
            if (existingAppointment != null && !existingAppointment.getId().equals(appointmentId)) {
                return new BookingResponseDTO(false, "New time slot is already booked");
            }
            
            // Update appointment
            Optional<Service> serviceOpt = serviceRepository.findById(request.getServiceId());
            if (serviceOpt.isPresent()) {
                appointment.setService(serviceOpt.get());
            }
            appointment.setAppointmentDate(request.getAppointmentDate());
            appointment.setAppointmentTime(request.getAppointmentTime());
            appointment.setNotes(request.getNotes());
            
            Appointment savedAppointment = appointmentRepository.save(appointment);
            
            // TODO: Send update notification
            // notificationService.sendAppointmentUpdate(savedAppointment);
            
            return new BookingResponseDTO(savedAppointment);
            
        } catch (Exception e) {
            return new BookingResponseDTO(false, "Failed to update appointment: " + e.getMessage());
        }
    }
}
