package com.easyq.common.controller;

import com.easyq.admin.repository.ServiceRepository;
import com.easyq.common.model.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @GetMapping("/")
    public String home(Model model) {
        try {
            List<Service> services = serviceRepository.findByIsActive(true);
            model.addAttribute("services", services);
        } catch (Exception e) {
            logger.warn("Could not load services from database: {}", e.getMessage());
            // Provide empty list if database is not available
            model.addAttribute("services", new ArrayList<Service>());
        }
        model.addAttribute("title", "Easy-Q - Digital Queue & Appointment Manager");
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @PostMapping("/login")
    public String loginSubmit(@RequestParam String username, @RequestParam String password, Model model) {
        // Simple login validation - in production, use proper authentication
        if ("admin".equals(username) && "admin123".equals(password)) {
            // Redirect to notifications page after successful login
            return "redirect:/notifications/";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}
