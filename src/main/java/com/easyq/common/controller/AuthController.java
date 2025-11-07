package com.easyq.common.controller;

import com.easyq.admin.repository.UserRepository;
import com.easyq.common.model.User;
import com.easyq.common.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }
    
    @PostMapping("/signup")
    public String signupBasicDetails(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String phone,
            HttpSession session,
            Model model) {
        
        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already exists");
            model.addAttribute("user", new User());
            return "signup";
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email already exists");
            model.addAttribute("user", new User());
            return "signup";
        }
        
        // Store basic details in session for profile completion
        session.setAttribute("signup_username", username);
        session.setAttribute("signup_email", email);
        session.setAttribute("signup_password", passwordEncoder.encode(password));
        session.setAttribute("signup_firstName", firstName);
        session.setAttribute("signup_lastName", lastName);
        session.setAttribute("signup_phone", phone);
        
        return "redirect:/signup/profile";
    }
    
    @GetMapping("/signup/profile")
    public String profilePage(HttpSession session, Model model) {
        if (session.getAttribute("signup_username") == null) {
            return "redirect:/signup";
        }
        return "signup-profile";
    }
    
    @PostMapping("/signup/profile")
    public String completeSignup(
            @RequestParam(required = false) MultipartFile profilePicture,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Retrieve basic details from session
            String username = (String) session.getAttribute("signup_username");
            String email = (String) session.getAttribute("signup_email");
            String password = (String) session.getAttribute("signup_password");
            String firstName = (String) session.getAttribute("signup_firstName");
            String lastName = (String) session.getAttribute("signup_lastName");
            String phone = (String) session.getAttribute("signup_phone");
            
            if (username == null) {
                return "redirect:/signup";
            }
            
            // Create user
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            user.setRole(User.Role.CUSTOMER);
            user.setIsActive(true);
            
            // Handle profile picture upload
            if (profilePicture != null && !profilePicture.isEmpty()) {
                try {
                    String profilePicturePath = fileStorageService.storeFile(profilePicture);
                    user.setProfilePicture(profilePicturePath);
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "Failed to upload profile picture: " + e.getMessage());
                    return "redirect:/signup/profile";
                }
            }
            
            // Save user
            userRepository.save(user);
            
            // Clear session
            session.removeAttribute("signup_username");
            session.removeAttribute("signup_email");
            session.removeAttribute("signup_password");
            session.removeAttribute("signup_firstName");
            session.removeAttribute("signup_lastName");
            session.removeAttribute("signup_phone");
            
            redirectAttributes.addFlashAttribute("success", "Account created successfully! Please login.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create account: " + e.getMessage());
            return "redirect:/signup/profile";
        }
    }
}

