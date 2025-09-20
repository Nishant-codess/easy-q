package com.easyq.admin.controller;

import com.easyq.admin.dto.DashboardStatsDTO;
import com.easyq.admin.service.AdminService;
import com.easyq.common.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping
    public String dashboard(Model model) {
        DashboardStatsDTO stats = adminService.getDashboardStats();
        model.addAttribute("stats", stats);
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @GetMapping("/services")
    public String services(Model model) {
        List<Service> services = adminService.getAllServices();
        model.addAttribute("services", services);
        return "admin/services";
    }
    
    @GetMapping("/appointments")
    public String appointments(Model model) {
        List<Appointment> appointments = adminService.getAllAppointments();
        model.addAttribute("appointments", appointments);
        return "admin/appointments";
    }
    
    @GetMapping("/queue")
    public String queue(Model model) {
        List<QueueEntry> queueEntries = adminService.getAllQueueEntries();
        model.addAttribute("queueEntries", queueEntries);
        return "admin/queue";
    }
    
    // API endpoints for AJAX calls
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<DashboardStatsDTO> getStats() {
        DashboardStatsDTO stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/api/users")
    @ResponseBody
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = adminService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }
    
    @PutMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updatedUser = adminService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/api/services")
    @ResponseBody
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        Service createdService = adminService.createService(service);
        return ResponseEntity.ok(createdService);
    }
    
    @PutMapping("/api/services/{id}")
    @ResponseBody
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody Service service) {
        service.setId(id);
        Service updatedService = adminService.updateService(service);
        return ResponseEntity.ok(updatedService);
    }
    
    @DeleteMapping("/api/services/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        adminService.deleteService(id);
        return ResponseEntity.ok().build();
    }
}
