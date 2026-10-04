package com.example.myapp;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;

    SupportRequestService(SupportRequestRepository supportRequestRepository) {
        this.supportRequestRepository = supportRequestRepository;
    }

    // SUBMIT: creates a new request and auto-assigns the case number
    public SupportRequest submitRequest(SupportRequest request) {
        request.setId(null); // never trust an id from the client
        request.setDateSubmitted(LocalDate.now());
        request.setStatus(RequestStatus.SUBMITTED);
        request.setCaseNumber(generateCaseNumber());
        return supportRequestRepository.save(request);
    }

    public List<SupportRequest> getAllRequests() {
        return supportRequestRepository.findAll();
    }

    public Optional<SupportRequest> getRequestById(Long id) {
        return supportRequestRepository.findById(id);
    }

    // EDIT: updates the editable fields only — case number, id, and submitted date never change
    public SupportRequest editRequest(Long id, SupportRequest updated) {
        SupportRequest existing = supportRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No support request found with id " + id));

        existing.setReportedAt(updated.getReportedAt());
        existing.setDescription(updated.getDescription());
        existing.setIssueCategory(updated.getIssueCategory());
        existing.setUrgencyLevel(updated.getUrgencyLevel());
        existing.setPriorityLevel(updated.getPriorityLevel());

        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }

        return supportRequestRepository.save(existing);
    }

    // CANCEL: soft-cancel, keeps the record for reporting instead of deleting it
    public SupportRequest cancelRequest(Long id) {
        SupportRequest existing = supportRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No support request found with id " + id));
        existing.setStatus(RequestStatus.CANCELLED);
        return supportRequestRepository.save(existing);
    }

    // FOLLOW-UP: request/confirm a follow-up session date
    public SupportRequest scheduleFollowUp(Long id, LocalDateTime scheduleDate) {
        SupportRequest existing = supportRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No support request found with id " + id));
        existing.setFollowUpRequired(true);
        existing.setRequestedScheduleDate(scheduleDate);
        existing.setStatus(RequestStatus.SCHEDULED);
        return supportRequestRepository.save(existing);
    }

    // SESSION NOTES: counsellor/social worker adds notes after a session
    public SupportRequest addSessionNotes(Long id, String notes) {
        SupportRequest existing = supportRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No support request found with id " + id));
        existing.setSessionNotes(notes);
        return supportRequestRepository.save(existing);
    }

    // DASHBOARD: how many cases of each issue type were reported this calendar month.
    // Used to draw the bar chart on the Counsellor/Admin dashboard.
    public Map<String, Long> getIssueCountsForCurrentMonth() {
        LocalDate now = LocalDate.now();
        return supportRequestRepository.findAll().stream()
                .filter(r -> r.getDateSubmitted() != null
                        && r.getDateSubmitted().getMonthValue() == now.getMonthValue()
                        && r.getDateSubmitted().getYear() == now.getYear())
                .collect(Collectors.groupingBy(
                        r -> r.getIssueCategory() != null ? r.getIssueCategory().name() : "OTHER",
                        Collectors.counting()));
    }

    // Format: CASE-2026-0001, resets numbering each year.
    // Note: countByDateSubmitted isn't concurrency-safe under simultaneous submissions;
    // fine for now, but if this ever needs to survive two people submitting in the same
    // instant, swap this for a DB sequence.
    private String generateCaseNumber() {
        int year = LocalDate.now().getYear();
        long countThisYear = supportRequestRepository.findAll().stream()
                .filter(r -> r.getDateSubmitted() != null && r.getDateSubmitted().getYear() == year)
                .count();
        long nextNumber = countThisYear + 1;
        return String.format("CASE-%d-%04d", year, nextNumber);
    }
}
