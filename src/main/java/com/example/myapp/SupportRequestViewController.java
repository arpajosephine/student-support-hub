package com.example.myapp;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SupportRequestViewController {

    private final UserRepository userRepository;

    SupportRequestViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Visit http://localhost:8080/support-request-form
    // (Counsellors and Admins only — see SecurityConfig)
    @GetMapping("/support-request-form")
    public String showForm(Model model, Authentication authentication) {
        String displayName = userRepository.findByUsername(authentication.getName())
                .map(User::getFullName)
                .orElse(authentication.getName());
        model.addAttribute("username", displayName);
        return "support-request-form";
    }
}
