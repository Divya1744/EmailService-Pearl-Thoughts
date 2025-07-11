package com.divya.emailservice.service;

import com.divya.emailservice.model.EmailRequest;
import com.divya.emailservice.model.EmailStatus;
import com.divya.emailservice.provider.MockEmailProvider1;
import com.divya.emailservice.provider.MockEmailProvider2;
import com.divya.emailservice.util.RateLimiter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class EmailServiceImple implements EmailService {
    private final MockEmailProvider1 provider1 = new MockEmailProvider1();
    private final MockEmailProvider2 provider2 = new MockEmailProvider2();
    private final RateLimiter rateLimiter = new RateLimiter();
    private final Map<String, EmailStatus> statusStore = new ConcurrentHashMap<>(); // for idempotency + tracking


    @Override
    public EmailStatus sendEmail(EmailRequest request) {

        // Idempotency check
        /* Reusing the same emailId for different recipients or messages will not work correctly.
        The client must use a new emailId for every unique email to be sent.*/

        EmailStatus existing = statusStore.get(request.getEmailId());
        if (existing != null && !existing.getTo().equals(request.getTo())) {
            throw new IllegalArgumentException("emailId reused for different recipient");
        }
        if (statusStore.containsKey(request.getEmailId())) {
            return statusStore.get(request.getEmailId());
        }


        int maxAttempts = 3;
        int delay = 1000; // start with 1 second, the double later for exponential back off
        boolean sent = false;
        String providerUsed = "Provider1";
        int attempts = 0;

        // Try Provider1
        for (int i = 1; i <= maxAttempts; i++) {
            attempts++;
            System.out.println("Attempt " + i + ": Sending with Primary Provider...");
            System.out.println("Sending email to: " + request.getTo() + " using provider: " + providerUsed);
            if(!rateLimiter.isAllowed(request.getTo())){
                System.out.println("Rate limit exceeded for: " + request.getTo());
                EmailStatus status = new EmailStatus();
                status.setEmailId(request.getEmailId());
                status.setTo(request.getTo());
                status.setStatus("RATE_LIMITED");
                status.setProvider("N/A");
                status.setTotalAttempts(0);
                status.setTimestamp(LocalDateTime.now());
                return status;
            }

            sent = provider1.send(request.getTo(), request.getSubject(), request.getBody());
            if(sent){
                System.out.println("Email sent successfully using Primary Provider.");
                break;
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {
            }
            delay *= 2; // exponential backoff
            System.out.println("Retrying... attempt " + attempts + " after " + delay/1000 + "secs");
        }

        // Fallback to Provider2
        if (!sent) {
            System.out.println("All primary provider attempts failed. Switching to Fallback Provider...");
            providerUsed = "Provider2";
            delay = 1000;
            for (int i = 1; i <= maxAttempts; i++) {

                attempts++;
                System.out.println("Attempt " + i + ": Sending with Fallback Provider...");
                if(!rateLimiter.isAllowed(request.getTo())){
                    System.out.println("Rate limit exceeded for: " + request.getTo());
                    EmailStatus status = new EmailStatus();
                    status.setEmailId(request.getEmailId());
                    status.setTo(request.getTo());
                    status.setStatus("RATE_LIMITED");
                    status.setProvider("N/A");
                    status.setTotalAttempts(0);
                    status.setTimestamp(LocalDateTime.now());
                    return status;
                }

                sent = provider2.send(request.getTo(), request.getSubject(), request.getBody());
                if(sent){
                    System.out.println("Email sent successfully using Fallback Provider.");
                    break;
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {
                }

                delay *= 2;
                System.out.println("Retrying... attempt " + attempts + " after " + delay/1000 + "secs");

            }
        }

        // Create status
        EmailStatus status = new EmailStatus();
        status.setEmailId(request.getEmailId());
        status.setStatus(sent ? "SENT" : "FAILED");
        status.setProvider(providerUsed);
        status.setTotalAttempts(attempts);
        status.setTimestamp(LocalDateTime.now());
        status.setTo(request.getTo());


        // Store it
        statusStore.put(request.getEmailId(), status);

        System.out.println("Final email status: " + status.getStatus() +
                " | Provider: " + status.getProvider() +
                " | Attempts: " + attempts);

        return status;
    }

    @Override
    public EmailStatus getStatusById(String emailId) {
        return statusStore.get(emailId);
    }
}
