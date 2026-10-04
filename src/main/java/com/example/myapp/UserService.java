package com.example.myapp;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Public self-signup always creates a STUDENT account.
    public User registerStudent(String fullName, String username, String email, String password) {
        User user = new User(fullName, username, email, password, Role.STUDENT);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    // Hashes the plain-text password before saving.
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getStaffContacts() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() != Role.STUDENT && u.isActive())
                .toList();
    }

    public User setActive(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No user found with id " + id));
        user.setActive(active);
        return userRepository.save(user);
    }

    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No user found with username " + username));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
