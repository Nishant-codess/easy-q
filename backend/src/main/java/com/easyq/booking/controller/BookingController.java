package com.easyq.booking.controller;

import com.easyq.booking.dto.BookingRequestDTO;
import com.easyq.booking.dto.BookingResponseDTO;
import com.easyq.booking.service.BookingService;
import com.easyq.common.model.Appointment;
import com.easyq.common.model.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/booking")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private com.easyq.admin.repository.ServiceRepository serviceRepository;
    
    @Autowired
    private com.easyq.admin.repository.UserRepository userRepository;
    
    @GetMapping
    public String bookingPage(Model model) {
        List<Service> services = serviceRepository.findByIsActive(true);
        model.addAttribute("services", services);
        model.addAttribute("bookingRequest", new BookingRequestDTO());
        return "booking/booking";
    }
    
    @GetMapping("/my-appointments")
    public String myAppointments(Model model) {
        // For demo purposes, using user ID 4 (customer1)
        Long userId = 4L;
        List<Appointment> appointments = bookingService.getUserAppointments(userId);
        model.addAttribute("appointments", appointments);
        return "booking/my-appointments";
    }
    
    @GetMapping("/calendar")
    public String calendar(Model model) {
        LocalDate today = LocalDate.now();
        List<Appointment> todayAppointments = bookingService.getAppointmentsByDate(today);
        model.addAttribute("appointments", todayAppointments);
        model.addAttribute("selectedDate", today);
        return "booking/calendar";
    }
    
    @PostMapping("/book")
    @ResponseBody
    public ResponseEntity<BookingResponseDTO> bookAppointment(@RequestBody BookingRequestDTO request) {
        // For demo purposes, using user ID 4 (customer1)
        Long userId = 4L;
        BookingResponseDTO response = bookingService.bookAppointment(request, userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<BookingResponseDTO> updateAppointment(@PathVariable Long id, @RequestBody BookingRequestDTO request) {
        // For demo purposes, using user ID 4 (customer1)
        Long userId = 4L;
        BookingResponseDTO response = bookingService.updateAppointment(id, request, userId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<BookingResponseDTO> cancelAppointment(@PathVariable Long id) {
        // For demo purposes, using user ID 4 (customer1)
        Long userId = 4L;
        BookingResponseDTO response = bookingService.cancelAppointment(id, userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api/appointments")
    @ResponseBody
    public ResponseEntity<List<Appointment>> getAppointments(@RequestParam(required = false) LocalDate date) {
        List<Appointment> appointments;
        if (date != null) {
            appointments = bookingService.getAppointmentsByDate(date);
        } else {
            // For demo purposes, using user ID 4 (customer1)
            Long userId = 4L;
            appointments = bookingService.getUserAppointments(userId);
        }
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/api/available-slots")
    @ResponseBody
    public ResponseEntity<List<String>> getAvailableSlots(@RequestParam LocalDate date, @RequestParam Long serviceId) {
        // TODO: Implement logic to get available time slots for a specific date and service
        // This is a placeholder implementation
        List<String> availableSlots = List.of(
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"
        );
        return ResponseEntity.ok(availableSlots);
    }
}
