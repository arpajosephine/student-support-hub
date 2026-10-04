package com.example.myapp;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminUsersViewController {

    private final UserRepository userRepository;

    AdminUsersViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/users")
    public String manageUsers(Model model, Authentication authentication) {
        String displayName = userRepository.findByUsername(authentication.getName())
                .map(User::getFullName)
                .orElse(authentication.getName());
        model.addAttribute("username", displayName);
        return "manage-users";
    }
}
