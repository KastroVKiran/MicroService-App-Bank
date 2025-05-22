package com.bankapp.notification.controller;

import com.bankapp.notification.model.Notification;
import com.bankapp.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }
    
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Map<String, Object> request) {
        Notification notification = new Notification();
        notification.setUserId(Long.valueOf(request.get("userId").toString()));
        notification.setMessage((String) request.get("message"));
        
        String type = (String) request.get("type");
        notification.setType(Notification.NotificationType.valueOf(type));
        
        if (request.containsKey("channel")) {
            notification.setChannel((String) request.get("channel"));
        }
        
        return new ResponseEntity<>(notificationService.createNotification(notification), HttpStatus.CREATED);
    }
    
    @PostMapping("/resend-failed")
    public ResponseEntity<String> resendFailedNotifications() {
        notificationService.resendFailedNotifications();
        return ResponseEntity.ok("Resending failed notifications initiated");
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notification Service is up and running!");
    }
}