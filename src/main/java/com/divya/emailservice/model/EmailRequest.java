package com.divya.emailservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    private String emailId;
    private String to;
    private String subject;
    private String body;
}
