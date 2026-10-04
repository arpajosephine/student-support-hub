package com.example.myapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentViewController {

    @GetMapping("/student-request-form")
    public String studentForm() {
        return "student-request-form"; // resolves to templates/student-request-form.html
    }
}
