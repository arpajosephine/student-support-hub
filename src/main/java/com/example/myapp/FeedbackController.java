package com.example.myapp;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;

    FeedbackController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // Any logged-in user (Student, Counsellor, Admin) can send feedback.
    @PostMapping
    public Feedback submit(@RequestBody Map<String, String> body, Authentication authentication) {
        Feedback feedback = new Feedback(authentication.getName(), body.get("message"));
        return feedbackRepository.save(feedback);
    }

    // Admin-only — see SecurityConfig.
    @GetMapping
    public List<Feedback> getAll() {
        return feedbackRepository.findAllByOrderBySubmittedAtDesc();
    }
}
