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

        // Send 5 allowed emails
        for (int i = 1; i <= 5; i++) {
            EmailRequest request = new EmailRequest();
            request.setEmailId("rate" + i); // unique emailId for idempotency
            request.setTo(to);
            request.setSubject("Rate Test");
            request.setBody("Email " + i);

            EmailStatus status = emailService.sendEmail(request);
            assertEquals("SENT", status.getStatus(), "Should be allowed");
        }

        // 6th email should be rate-limited
        EmailRequest sixthRequest = new EmailRequest();
        sixthRequest.setEmailId("rate6");
        sixthRequest.setTo(to);
        sixthRequest.setSubject("Blocked Email");
        sixthRequest.setBody("This should be rate limited");

        EmailStatus sixthStatus = emailService.sendEmail(sixthRequest);
        assertEquals("RATE_LIMITED", sixthStatus.getStatus(), "Should be rate-limited on 6th attempt");
    }


}

