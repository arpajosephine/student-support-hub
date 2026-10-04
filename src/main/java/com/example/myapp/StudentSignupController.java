package com.example.myapp;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class StudentSignupController {

    private final UserService userService;

    StudentSignupController(UserService userService) {
        this.userService = userService;
    }

    // Public signup page — no login required.
    @GetMapping("/register")
    public String signupPage() {
        return "register";
    }

    // Public signup endpoint. Always creates a STUDENT account — role is never
    // taken from the request body, so this can't be used to create a Counsellor/Admin account.
    @PostMapping("/api/students/register")
    @ResponseBody
    public ResponseEntity<String> register(@RequestBody Map<String, String> body) {
        try {
            userService.registerStudent(body.get("fullName"), body.get("username"), body.get("email"), body.get("password"));
            return ResponseEntity.ok("Account created.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("That username or email is already registered.");
        }
    }
}
