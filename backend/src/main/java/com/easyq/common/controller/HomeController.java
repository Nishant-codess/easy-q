package com.easyq.common.controller;

import com.easyq.admin.repository.ServiceRepository;
import com.easyq.common.model.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @GetMapping("/")
    public String home(Model model) {
        List<Service> services = serviceRepository.findByIsActive(true);
        model.addAttribute("services", services);
        model.addAttribute("title", "Easy-Q - Digital Queue & Appointment Manager");
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
