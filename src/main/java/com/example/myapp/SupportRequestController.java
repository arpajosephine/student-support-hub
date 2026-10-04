package com.example.myapp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support-requests")
public class SupportRequestController {

    private final SupportRequestService supportRequestService;

    SupportRequestController(SupportRequestService supportRequestService) {
        this.supportRequestService = supportRequestService;
    }

    // SUBMIT
    @PostMapping
    public SupportRequest submit(@RequestBody SupportRequest request) {
        return supportRequestService.submitRequest(request);
    }

    @GetMapping
    public List<SupportRequest> getAll() {
        return supportRequestService.getAllRequests();
    }

    // DASHBOARD: issue counts for the current month, for the bar chart.
    @GetMapping("/stats/monthly-issues")
    public Map<String, Long> monthlyIssueStats() {
        return supportRequestService.getIssueCountsForCurrentMonth();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportRequest> getById(@PathVariable Long id) {
        return supportRequestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // EDIT
    @PutMapping("/{id}")
    public SupportRequest edit(@PathVariable Long id, @RequestBody SupportRequest request) {
        return supportRequestService.editRequest(id, request);
    }

    // CANCEL
    @PatchMapping("/{id}/cancel")
    public SupportRequest cancel(@PathVariable Long id) {
        return supportRequestService.cancelRequest(id);
    }

    // FOLLOW-UP / REQUEST SCHEDULE — body: {"scheduleDate": "2026-08-25T14:00:00"}
    @PatchMapping("/{id}/schedule")
    public SupportRequest scheduleFollowUp(@PathVariable Long id, @RequestBody Map<String, String> body) {
        LocalDateTime scheduleDate = LocalDateTime.parse(body.get("scheduleDate"));
        return supportRequestService.scheduleFollowUp(id, scheduleDate);
    }

    // SESSION NOTES — body: {"notes": "..."}
    @PatchMapping("/{id}/notes")
    public SupportRequest addNotes(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return supportRequestService.addSessionNotes(id, body.get("notes"));
    }
}
