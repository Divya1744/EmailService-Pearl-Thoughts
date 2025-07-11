package com.divya.emailservice.controller;

import com.divya.emailservice.model.EmailRequest;
import com.divya.emailservice.model.EmailStatus;
import com.divya.emailservice.service.EmailService;
import com.divya.emailservice.service.EmailServiceImple;
import com.divya.emailservice.util.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final RateLimiter rateLimiter;

    @PostMapping("/send")
    public ResponseEntity<EmailStatus> sendEmail(@RequestBody EmailRequest request) {
        EmailStatus status = emailService.sendEmail(request);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/status/{emailId}")
    public ResponseEntity<EmailStatus> getStatus(@PathVariable String emailId) {
        EmailStatus status = emailService.getStatusById(emailId);
        if (status != null) {
            return ResponseEntity.ok(status);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
