package com.example.myapp;

public class StaffContact {
    private String fullName;
    private String email;
    private String role;

    public StaffContact(User user) {
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.role = user.getRole().name();
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
