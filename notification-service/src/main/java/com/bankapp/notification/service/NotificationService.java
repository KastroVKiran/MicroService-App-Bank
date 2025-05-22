package com.bankapp.notification.service;

import com.bankapp.notification.model.Notification;
import com.bankapp.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private EmailService emailService;
    
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }
    
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus(Notification.NotificationStatus.PENDING);
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Send notification asynchronously
        sendNotification(savedNotification);
        
        return savedNotification;
    }
    
    public void sendNotification(Notification notification) {
        try {
            // In a real application, this would send via the appropriate channel
            // For simplicity, we'll just simulate sending an email
            emailService.sendEmail(notification.getUserId(), notification.getMessage());
            
            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
        }
        
        notificationRepository.save(notification);
    }
    
    public void resendFailedNotifications() {
        List<Notification> failedNotifications = 
            notificationRepository.findByStatus(Notification.NotificationStatus.FAILED);
        
        for (Notification notification : failedNotifications) {
            sendNotification(notification);
        }
    }
}