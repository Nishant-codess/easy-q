package com.easyq.common.config;

import com.easyq.admin.repository.ServiceRepository;
import com.easyq.admin.repository.UserRepository;
import com.easyq.common.model.Service;
import com.easyq.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create Nishant Ranjan - Customer User
        if (!userRepository.findByUsername("nishant").isPresent()) {
            User nishant = new User();
            nishant.setUsername("nishant");
            nishant.setEmail("nishant.ranjan@example.com");
            nishant.setPassword(passwordEncoder.encode("nishant123"));
            nishant.setFirstName("Nishant");
            nishant.setLastName("Ranjan");
            nishant.setPhone("+1234567890");
            nishant.setRole(User.Role.CUSTOMER);
            nishant.setIsActive(true);
            userRepository.save(nishant);
            System.out.println("Created user: nishant / nishant123");
        }
        
        // Create Dental Service Admin
        if (!userRepository.findByUsername("dental_admin").isPresent()) {
            User dentalAdmin = new User();
            dentalAdmin.setUsername("dental_admin");
            dentalAdmin.setEmail("admin@dentalservice.com");
            dentalAdmin.setPassword(passwordEncoder.encode("dental123"));
            dentalAdmin.setFirstName("Dental");
            dentalAdmin.setLastName("Admin");
            dentalAdmin.setPhone("+1234567891");
            dentalAdmin.setRole(User.Role.ADMIN);
            dentalAdmin.setIsActive(true);
            userRepository.save(dentalAdmin);
            System.out.println("Created admin: dental_admin / dental123");
        }
        
        // Create Dental Service if it doesn't exist
        if (serviceRepository.findByName("Dental Service") == null) {
            Service dentalService = new Service();
            dentalService.setName("Dental Service");
            dentalService.setDescription("Comprehensive dental care including checkups, cleanings, fillings, and oral health consultations");
            dentalService.setDurationMinutes(45);
            dentalService.setPrice(java.math.BigDecimal.valueOf(150.00));
            dentalService.setIsActive(true);
            serviceRepository.save(dentalService);
            System.out.println("Created service: Dental Service");
        }
    }
}

