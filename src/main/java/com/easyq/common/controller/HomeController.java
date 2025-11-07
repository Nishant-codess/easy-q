package com.easyq.common.controller;

import com.easyq.admin.repository.ServiceRepository;
import com.easyq.admin.repository.UserRepository;
import com.easyq.common.model.Service;
import com.easyq.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
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
    
    @GetMapping("/home")
    public String userHome(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOpt = userRepository.findByUsername(auth.getName());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("user", user);
                model.addAttribute("title", "Welcome to Easy-Q");
                
                // Get user's appointments and queue entries count
                try {
                    List<Service> services = serviceRepository.findByIsActive(true);
                    model.addAttribute("services", services);
                } catch (Exception e) {
                    model.addAttribute("services", new ArrayList<Service>());
                }
                
                return "user-home";
            }
        }
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        return "login";
    }
}
