package com.example.back_end.repository;

import com.example.back_end.model.entity.EmailDetails;

public interface EmailService {
    String sendSimpleMail(EmailDetails details);

    // Method
    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);
}
