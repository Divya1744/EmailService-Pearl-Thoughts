package com.divya.emailservice.service;

import com.divya.emailservice.model.EmailRequest;
import com.divya.emailservice.model.EmailStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceImplTest {

    private EmailServiceImple emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImple();
    }

    @Test
    void testEmailIsSentSuccessfully() {
        EmailRequest request = new EmailRequest();
        request.setEmailId("unit1");
        request.setTo("vijay@gmail.com");
        request.setSubject("Unit Test");
        request.setBody("Testing email send");

        EmailStatus status = emailService.sendEmail(request);

        assertEquals("SENT", status.getStatus());
        assertEquals("unit1", status.getEmailId());
        assertEquals("vijay@gmail.com", status.getTo());
    }

    @Test
    void testFallbackToSecondProvider() {
        EmailRequest request = new EmailRequest();
        request.setEmailId("fallback1");
        request.setTo("fallback@gmail.com"); // simulate failure
        request.setSubject("Fallback Test");
        request.setBody("Testing fallback logic");

        EmailStatus status = emailService.sendEmail(request);

        assertEquals("SENT", status.getStatus(), "Email should be SENT even after fallback");
        assertEquals("Provider2", status.getProvider(), "Should have used the fallback provider");
        assertEquals("fallback1", status.getEmailId());
    }

    @Test
    void AllProviderFails(){
        EmailRequest request = new EmailRequest();
        request.setEmailId("failAll");
        request.setTo("failAll@gmail.com"); // simulate failure
        request.setSubject("Fail Test");
        request.setBody("Testing fail logic");

        EmailStatus status = emailService.sendEmail(request);

        assertEquals("FAILED", status.getStatus(), "Email should be SENT even after fallback");
        assertEquals("Provider2", status.getProvider(), "Should have used the fallback provider");
        assertEquals("failAll", status.getEmailId());

    }

    @Test
    void testRateLimiting() {
        String to = "rate@test.com";

        // Send 6 allowed emails (limit is 6)
        for (int i = 1; i <= 6; i++) {
            EmailRequest request = new EmailRequest();
            request.setEmailId("rate" + i); // unique emailId
            request.setTo(to);
            request.setSubject("Rate Test");
            request.setBody("Email " + i);

            EmailStatus status = emailService.sendEmail(request);
            assertEquals("SENT", status.getStatus(), "Should be allowed");
        }

        // 7th email should be rate-limited
        EmailRequest seventhRequest = new EmailRequest();
        seventhRequest.setEmailId("rate7");
        seventhRequest.setTo(to);
        seventhRequest.setSubject("Blocked Email");
        seventhRequest.setBody("This should be rate limited");

        EmailStatus seventhStatus = emailService.sendEmail(seventhRequest);
        assertEquals("RATE_LIMITED", seventhStatus.getStatus(), "Should be rate-limited on 7th attempt");
    }



}

