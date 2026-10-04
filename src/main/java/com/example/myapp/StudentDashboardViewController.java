package com.example.myapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentDashboardViewController {

    @GetMapping("/student-dashboard")
    public String studentDashboard() {
        return "student-dashboard";
    }
}
