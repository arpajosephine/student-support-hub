package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    long countByDateSubmitted(java.time.LocalDate dateSubmitted);
}
