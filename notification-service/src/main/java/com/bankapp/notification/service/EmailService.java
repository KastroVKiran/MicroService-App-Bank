package com.bankapp.notification.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    public void sendEmail(Long userId, String message) {
        // In a real application, this would connect to an email service
        // For now, we'll just simulate sending an email
        System.out.println("Sending email to user " + userId + ": " + message);
        
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}