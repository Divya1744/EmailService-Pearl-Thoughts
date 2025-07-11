package com.divya.emailservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailStatus {
    private String emailId;
    private String to;
    private String status; // SENT / FAILED / RATE_LIMITED
    private String provider;
    private int totalAttempts;
    private LocalDateTime timestamp;
}
