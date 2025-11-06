package com.easyq.booking.controller;

import com.easyq.booking.dto.BookingRequestDTO;
import com.easyq.booking.dto.BookingResponseDTO;
import com.easyq.booking.service.BookingService;
import com.easyq.common.model.Appointment;
import com.easyq.common.model.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.easyq.common.model.User;
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
        Long userId = getOrCreateDemoUserId();
        List<Appointment> appointments = bookingService.getUserAppointments(userId);
        model.addAttribute("appointments", appointments);
        List<Service> services = serviceRepository.findByIsActive(true);
        model.addAttribute("services", services);
        return "booking/my-appointments";
    }
    
    @GetMapping("/calendar")
    public String calendar(Model model, @RequestParam(required = false) LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now();
        List<Appointment> dayAppointments = bookingService.getAppointmentsByDate(target);
        model.addAttribute("appointments", dayAppointments);
        model.addAttribute("selectedDate", target);
        return "booking/calendar";
    }
    
    @PostMapping("/book")
    @ResponseBody
    public ResponseEntity<BookingResponseDTO> bookAppointment(@RequestBody BookingRequestDTO request) {
        Long userId = getOrCreateDemoUserId();
        BookingResponseDTO response = bookingService.bookAppointment(request, userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<BookingResponseDTO> updateAppointment(@PathVariable Long id, @RequestBody BookingRequestDTO request) {
        Long userId = getOrCreateDemoUserId();
        BookingResponseDTO response = bookingService.updateAppointment(id, request, userId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<BookingResponseDTO> cancelAppointment(@PathVariable Long id) {
        Long userId = getOrCreateDemoUserId();
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
            Long userId = getOrCreateDemoUserId();
            appointments = bookingService.getUserAppointments(userId);
        }
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/api/available-slots")
    @ResponseBody
    public ResponseEntity<List<String>> getAvailableSlots(@RequestParam LocalDate date, @RequestParam Long serviceId) {
        List<String> availableSlots = bookingService.getAvailableSlots(date, serviceId);
        return ResponseEntity.ok(availableSlots);
    }

    private Long getOrCreateDemoUserId() {
        List<User> customers = userRepository.findByRole(User.Role.CUSTOMER);
        if (!customers.isEmpty()) {
            return customers.get(0).getId();
        }
        User demo = new User("demo_customer", "demo.customer@example.com", "password", "Demo", "Customer");
        demo.setRole(User.Role.CUSTOMER);
        demo.setIsActive(true);
        return userRepository.save(demo).getId();
    }
}
