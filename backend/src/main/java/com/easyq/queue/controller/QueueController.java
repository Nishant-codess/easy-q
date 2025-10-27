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
            QueueEntryDTO queueEntry = queueService.callNext(serviceId); // this peice of code changes the next user statuis to in progress when it calls the next quueu id
            return ResponseEntity.ok(queueEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/start/{id}")// it diferent from call-next as it trigrered manually whereas call next is automatically triggered
    //and also for call-next Picks the next waiting user automatically for that particaular service but this Starts service for a specific user
    //there is diffference between call next and starting the service
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
            QueueEntryDTO queueEntry = queueService.completeService(id);//"In Progress" → "Completed"
            return ResponseEntity.ok(queueEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/api/entries")//show the list of all queue entries.
    @ResponseBody
    public ResponseEntity<List<QueueEntryDTO>> getQueueEntries(@RequestParam(required = false) Long serviceId) {
        List<QueueEntryDTO> queueEntries = queueService.getQueueEntries(serviceId);
        return ResponseEntity.ok(queueEntries);
    }
    
    @GetMapping("/api/user-entries")// for a particaular user display the current user’s queue.
    @ResponseBody
    public ResponseEntity<List<QueueEntryDTO>> getUserQueueEntries() {
        Long userId = 4L;
        List<QueueEntryDTO> queueEntries = queueService.getUserQueueEntries(userId);//queue entries belonging to this user
        return ResponseEntity.ok(queueEntries);
    }
}
//Purpose of QueueController
// QueueController is the entry point for HTTP requests related to queue management. Its main responsibilities:
// 1. Handle user actions (joining a queue, viewing their queue).
// 2. Handle staff/admin actions (call next user, start service, complete service).
// 3. Fetch queue information for frontend display.
// 4. It does not directly manipulate the database—it calls QueueService, which contains the business logic.

// 2️⃣ Breakdown of Methods

// A. Show Queue Page
// @GetMapping
// public String queuePage(Model model) ---- Fetches all active services (e.g., doctor consultation, banking).
// Adds them to the model: model.addAttribute("services", services).
// Returns the queue page view (queue/queue).
// PURPOSE: Display services so users can choose which queue to join.

// B. Show User’s Queue
// @GetMapping("/my-queue")
// public String myQueue(Model model)
// Hardcoded user ID (4L) for demo. Calls queueService.getUserQueueEntries(userId) → gets all queue entries for that user.
// Adds entries to the model → displayed on user dashboard.
// Purpose: Let a user see their position and status in the queue.

// C. Display Queue Board
// @GetMapping("/display")
// public String queueDisplay(Model model)
// Fetches all queue entries via queueService.getQueueEntries(null).
// Adds to model → used in frontend display board (waiting/in progress users).

// D. Join Queue
// @PostMapping("/join")
// @ResponseBody
// public ResponseEntity<QueueEntryDTO> joinQueue(@RequestParam Long serviceId)
// Hardcoded user ID (4L) for demo.
// Calls queueService.joinQueue(userId, serviceId) → creates a new queue entry with status "Waiting".
// Returns QueueEntryDTO as JSON → frontend can show confirmation and position.

// E. Call Next User
// @PostMapping("/call-next")
// @ResponseBody
// public ResponseEntity<QueueEntryDTO> callNext(@RequestParam Long serviceId)
// Picks the next waiting user in a service queue automatically.
// Changes their status → In Progress.
// Returns QueueEntryDTO → frontend can show “Now Serving”.
// Difference from /start/{id}: Automatically selects the next user instead of specifying a queue entry ID.

// F. Start Service
// @PostMapping("/start/{id}")
// @ResponseBody
// public ResponseEntity<QueueEntryDTO> startService(@PathVariable Long id)
// Starts service for a specific queue entry (ID from URL).
// Changes status Waiting → In Progress.
// Returns QueueEntryDTO → frontend shows the user is being served.

// G. Complete Service
// @PostMapping("/complete/{id}")
// @ResponseBody
// public ResponseEntity<QueueEntryDTO> completeService(@PathVariable Long id)
// Marks service as completed for a specific queue entry.
// Changes status In Progress → Completed.
// Returns QueueEntryDTO → frontend can remove from active queue display.

// H. API: Get All Queue Entries
// @GetMapping("/api/entries")
// @ResponseBody
// public ResponseEntity<List<QueueEntryDTO>> getQueueEntries(@RequestParam(required = false) Long serviceId)
// Fetches all queue entries.
// Can filter by serviceId (optional).
// Returns JSON list of queue entries for frontend display.

// I. API: Get Current User’s Queue
// @GetMapping("/api/user-entries")
// @ResponseBody
// public ResponseEntity<List<QueueEntryDTO>> getUserQueueEntries()
// Hardcoded user ID (4L) for demo.
// Returns queue entries for the logged-in user.
// Frontend can display user’s current queue position and status.

// 3️⃣ Flow of a Queue Entry
// Step	Method Called	Status Change
// User joins queue	/join	None → Waiting
// Staff calls next	/call-next	Waiting → In Progress
// Staff starts service	/start/{id}	Waiting → In Progress
// Staff completes service	/complete/{id}	In Progress → Completed
// Frontend uses QueueEntryDTO.status to display the status.
// QueueService handles the logic and updates the database via QueueEntryRepository.

// 4️⃣ Files Responsible for Status
// Responsibility	File/Layer
// Stores actual status	QueueEntry.java
// Updates status	QueueService.java
// Saves/Fetches from DB	QueueEntryRepository.java
// Sends status to frontend	QueueEntryDTO.java
// Triggers status changes	QueueController.java

// 5️⃣ Summary of QueueController
// Handles all HTTP requests for queue management.
// Uses QueueService for business logic.
// Returns QueueEntryDTO or renders views for frontend.
// Supports:
// User joining a queue
// Viewing user queue
// Viewing queue display board
// Staff calling next user
// Staff starting service
// Staff completing service
// API endpoints for fetching queue entries
// In short:
// QueueController is the bridge between frontend and backend for queue operations. It receives requests, calls QueueService to update or fetch queue info, and returns the updated data or views to the frontend.