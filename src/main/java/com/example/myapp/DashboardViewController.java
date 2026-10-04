package com.example.myapp;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardViewController {

    private final UserRepository userRepository;

    DashboardViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String displayName = userRepository.findByUsername(authentication.getName())
                .map(User::getFullName)
                .orElse(authentication.getName());
        model.addAttribute("username", displayName);

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "dashboard";
    }
}
