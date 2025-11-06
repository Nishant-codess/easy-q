package com.easyq.booking.service;

import com.easyq.booking.dto.BookingRequestDTO;
import com.easyq.booking.dto.BookingResponseDTO;
import com.easyq.admin.repository.AppointmentRepository;
import com.easyq.common.model.Appointment;
import com.easyq.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class BookingService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private com.easyq.admin.repository.ServiceRepository serviceRepository;
    
    @Autowired
    private com.easyq.admin.repository.UserRepository userRepository;
    
    @Autowired
    private com.easyq.queue.service.QueueService queueService;
    
    public BookingResponseDTO bookAppointment(BookingRequestDTO request, Long userId) {
        try {
            // Validate service exists
            Optional<com.easyq.common.model.Service> serviceOpt = serviceRepository.findById(request.getServiceId());
            if (serviceOpt.isEmpty()) {
                return new BookingResponseDTO(false, "Service not found");
            }
            
            // Validate user exists
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return new BookingResponseDTO(false, "User not found");
            }
            
            // Check if time slot is available
            Optional<Appointment> existingAppointmentOpt = appointmentRepository.findByAppointmentDateAndAppointmentTime(
                request.getAppointmentDate(), request.getAppointmentTime());
            if (existingAppointmentOpt.isPresent()) {
                return new BookingResponseDTO(false, "Time slot is already booked");
            }
            
            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setUser(userOpt.get());
            appointment.setService(serviceOpt.get());
            appointment.setAppointmentDate(request.getAppointmentDate());
            appointment.setAppointmentTime(request.getAppointmentTime());
            appointment.setNotes(request.getNotes());
            appointment.setPatientName(request.getPatientName());
            appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
            
            Appointment savedAppointment = appointmentRepository.save(appointment);
            
            // Auto-queue dental checkup appointments
            if ("Dental Checkup".equalsIgnoreCase(serviceOpt.get().getName())) {
                queueService.joinQueue(userOpt.get().getId(), serviceOpt.get().getId());
            }
            
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
        return appointmentRepository.findByAppointmentDate(date);
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
            Optional<Appointment> existingAppointmentOpt = appointmentRepository.findByAppointmentDateAndAppointmentTime(
                request.getAppointmentDate(), request.getAppointmentTime());
            if (existingAppointmentOpt.isPresent() && !existingAppointmentOpt.get().getId().equals(appointmentId)) {
                return new BookingResponseDTO(false, "New time slot is already booked");
            }
            
            // Update appointment
            Optional<com.easyq.common.model.Service> serviceOpt = serviceRepository.findById(request.getServiceId());
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

    public List<String> getAvailableSlots(LocalDate date, Long serviceId) {
        // Determine slot length from service duration; default to 30 minutes
        int slotMinutes = serviceRepository.findById(serviceId)
            .map(s -> s.getDurationMinutes() != null ? s.getDurationMinutes() : 30)
            .orElse(30);

        // Define working hours (09:00 to 17:00)
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);

        // Collect already booked times for that day
        List<com.easyq.common.model.Appointment> dayAppointments = appointmentRepository.findByAppointmentDate(date);
        Set<LocalTime> bookedTimes = new HashSet<>();
        for (Appointment appt : dayAppointments) {
            bookedTimes.add(appt.getAppointmentTime());
        }

        List<String> slots = new ArrayList<>();
        for (LocalTime t = start; !t.plusMinutes(slotMinutes).isAfter(end); t = t.plusMinutes(slotMinutes)) {
            if (!bookedTimes.contains(t)) {
                slots.add(t.toString().substring(0,5));
            }
        }
        return slots;
    }
}
