package com.easyq.common.controller;

import com.easyq.admin.repository.ServiceRepository;
import com.easyq.admin.repository.UserRepository;
import com.easyq.common.model.Service;
import com.easyq.common.model.User;
import com.easyq.common.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/join-easyq")
public class AdminSignupController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @GetMapping
    public String signupPage(Model model) {
        return "admin-signup";
    }
    
    @PostMapping
    public String submitSignup(
            // Admin/Service Owner Details
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) MultipartFile profilePicture,
            
            // Service Details
            @RequestParam String serviceName,
            @RequestParam String serviceDescription,
            @RequestParam Integer durationMinutes,
            @RequestParam(required = false) Double price,
            
            RedirectAttributes redirectAttributes) {
        
        try {
            // Check if username already exists
            if (userRepository.findByUsername(username).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/join-easyq";
            }
            
            // Check if email already exists
            if (userRepository.findByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email already exists");
                return "redirect:/join-easyq";
            }
            
            // Create admin user
            User admin = new User();
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            admin.setPhone(phone);
            admin.setRole(User.Role.ADMIN);
            admin.setIsActive(true);
            
            // Handle profile picture upload
            if (profilePicture != null && !profilePicture.isEmpty()) {
                try {
                    String profilePicturePath = fileStorageService.storeFile(profilePicture);
                    admin.setProfilePicture(profilePicturePath);
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "Failed to upload profile picture: " + e.getMessage());
                    return "redirect:/join-easyq";
                }
            }
            
            // Save admin user
            userRepository.save(admin);
            
            // Create service
            Service service = new Service();
            service.setName(serviceName);
            service.setDescription(serviceDescription);
            service.setDurationMinutes(durationMinutes);
            if (price != null) {
                service.setPrice(java.math.BigDecimal.valueOf(price));
            }
            service.setIsActive(true);
            
            // Save service
            serviceRepository.save(service);
            
            redirectAttributes.addFlashAttribute("success", 
                "Your service has been registered successfully! You can now login with your credentials.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to register service: " + e.getMessage());
            return "redirect:/join-easyq";
        }
    }
}

