package com.example.myapp;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsViewController {

    private final UserRepository userRepository;

    SettingsViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/settings")
    public String settings(Model model, Authentication authentication) {
        String displayName = userRepository.findByUsername(authentication.getName())
                .map(User::getFullName)
                .orElse(authentication.getName());
        model.addAttribute("username", displayName);
        return "settings";
    }
}
