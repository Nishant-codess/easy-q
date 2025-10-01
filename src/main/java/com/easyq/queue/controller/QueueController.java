package com.easyq.queue.controller;

import com.easyq.queue.dto.QueueEntryDTO;
import com.easyq.queue.service.QueueService;
import com.easyq.common.model.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/queue")
public class QueueController {
    
    @Autowired
    private QueueService queueService;
    
    @Autowired
    private com.easyq.admin.repository.ServiceRepository serviceRepository;
    
    @GetMapping
    public String queuePage(Model model) {
        List<Service> services = serviceRepository.findByIsActive(true);
        model.addAttribute("services", services);
        return "queue/queue";
    }
    
    @GetMapping("/my-queue")
    public String myQueue(Model model) {
        Long userId = 4L;
        List<QueueEntryDTO> queueEntries = queueService.getUserQueueEntries(userId);
        model.addAttribute("queueEntries", queueEntries);
        return "queue/my-queue";
    }
    
    @GetMapping("/display")
    public String queueDisplay(Model model) {
        List<QueueEntryDTO> queueEntries = queueService.getQueueEntries(null);
        model.addAttribute("queueEntries", queueEntries);
        return "queue/display";
    }
    
    @PostMapping("/join")
    @ResponseBody
    public ResponseEntity<QueueEntryDTO> joinQueue(@RequestParam Long serviceId) {
        try {
            Long userId = 4L;
            QueueEntryDTO queueEntry = queueService.joinQueue(userId, serviceId);
            return ResponseEntity.ok(queueEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/call-next")
    @ResponseBody
    public ResponseEntity<QueueEntryDTO> callNext(@RequestParam Long serviceId) {
        try {
            QueueEntryDTO queueEntry = queueService.callNext(serviceId);
            return ResponseEntity.ok(queueEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/start/{id}")
    @ResponseBody
    public ResponseEntity<QueueEntryDTO> startService(@PathVariable Long id) {
        try {
            QueueEntryDTO queueEntry = queueService.startService(id);
            return ResponseEntity.ok(queueEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/complete/{id}")
    @ResponseBody
    public ResponseEntity<QueueEntryDTO> completeService(@PathVariable Long id) {
        try {
            QueueEntryDTO queueEntry = queueService.completeService(id);
            return ResponseEntity.ok(queueEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/api/entries")
    @ResponseBody
    public ResponseEntity<List<QueueEntryDTO>> getQueueEntries(@RequestParam(required = false) Long serviceId) {
        List<QueueEntryDTO> queueEntries = queueService.getQueueEntries(serviceId);
        return ResponseEntity.ok(queueEntries);
    }
    
    @GetMapping("/api/user-entries")
    @ResponseBody
    public ResponseEntity<List<QueueEntryDTO>> getUserQueueEntries() {
        // For demo purposes, using user ID 4 (customer1)
        Long userId = 4L;
        List<QueueEntryDTO> queueEntries = queueService.getUserQueueEntries(userId);
        return ResponseEntity.ok(queueEntries);
    }
}
