package com.divya.emailservice.service;

import com.divya.emailservice.model.EmailRequest;
import com.divya.emailservice.model.EmailStatus;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    EmailStatus sendEmail(EmailRequest request);
    EmailStatus getStatusById(String emailId);

}
