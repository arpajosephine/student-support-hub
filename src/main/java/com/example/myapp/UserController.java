package com.example.myapp;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    // Restricted to ADMIN in SecurityConfig — lets an admin see every account.
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Admin-only now (see SecurityConfig) — used by the Manage Users page to create accounts.
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // Admin-only — activate/deactivate an account. Body: {"active": true} or {"active": false}
    @PatchMapping("/{id}/status")
    public User setActive(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return userService.setActive(id, body.get("active"));
    }

    // Any logged-in user can change their own password from Settings. Body: {"newPassword": "..."}
    @PatchMapping("/me/password")
    public void changeOwnPassword(@RequestBody Map<String, String> body, Authentication authentication) {
        userService.changePassword(authentication.getName(), body.get("newPassword"));
    }

    // Counsellor/Admin only (see SecurityConfig) — powers the Dashboard's Contact section.
    @GetMapping("/staff-contacts")
    public List<StaffContact> getStaffContacts() {
        return userService.getStaffContacts().stream().map(StaffContact::new).toList();
    }
}
