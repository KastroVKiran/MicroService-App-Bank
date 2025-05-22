package com.bankapp.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GatewayController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${account.service.url}")
    private String accountServiceUrl;
    
    @Value("${transaction.service.url}")
    private String transactionServiceUrl;
    
    @Value("${auth.service.url}")
    private String authServiceUrl;
    
    @Value("${notification.service.url}")
    private String notificationServiceUrl;
    
    @Value("${user.service.url}")
    private String userServiceUrl;
    
    // Account Service Routes
    
    @GetMapping("/accounts")
    public ResponseEntity<Object> getAllAccounts(@RequestHeader HttpHeaders headers) {
        return restTemplate.exchange(
                accountServiceUrl + "/api/accounts",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Object.class
        );
    }
    
    @GetMapping("/accounts/{id}")
    public ResponseEntity<Object> getAccountById(
            @PathVariable String id,
            @RequestHeader HttpHeaders headers) {
        return restTemplate.exchange(
                accountServiceUrl + "/api/accounts/" + id,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Object.class
        );
    }
    
    @PostMapping("/accounts")
    public ResponseEntity<Object> createAccount(
            @RequestBody Map<String, Object> account,
            @RequestHeader HttpHeaders headers) {
        return restTemplate.exchange(
                accountServiceUrl + "/api/accounts",
                HttpMethod.POST,
                new HttpEntity<>(account, headers),
                Object.class
        );
    }
    
    // Transaction Service Routes
    
    @GetMapping("/transactions")
    public ResponseEntity<Object> getAllTransactions(@RequestHeader HttpHeaders headers) {
        return restTemplate.exchange(
                transactionServiceUrl + "/api/transactions",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Object.class
        );
    }
    
    @PostMapping("/transactions/deposit")
    public ResponseEntity<Object> deposit(
            @RequestBody Map<String, Object> transaction,
            @RequestHeader HttpHeaders headers) {
        return restTemplate.exchange(
                transactionServiceUrl + "/api/transactions/deposit",
                HttpMethod.POST,
                new HttpEntity<>(transaction, headers),
                Object.class
        );
    }
    
    @PostMapping("/transactions/withdraw")
    public ResponseEntity<Object> withdraw(
            @RequestBody Map<String, Object> transaction,
            @RequestHeader HttpHeaders headers) {
        return restTemplate.exchange(
                transactionServiceUrl + "/api/transactions/withdraw",
                HttpMethod.POST,
                new HttpEntity<>(transaction, headers),
                Object.class
        );
    }
    
    @PostMapping("/transactions/transfer")
    public ResponseEntity<Object> transfer(
            @RequestBody Map<String, Object> transaction,
            @RequestHeader HttpHeaders headers) {
        return restTemplate.exchange(
                transactionServiceUrl + "/api/transactions/transfer",
                HttpMethod.POST,
                new HttpEntity<>(transaction, headers),
                Object.class
        );
    }
    
    // Auth Service Routes
    
    @PostMapping("/auth/register")
    public ResponseEntity<Object> register(
            @RequestBody Map<String, Object> user) {
        return restTemplate.postForEntity(
                authServiceUrl + "/api/auth/register",
                user,
                Object.class
        );
    }
    
    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(
            @RequestBody Map<String, Object> credentials) {
        return restTemplate.postForEntity(
                authServiceUrl + "/api/auth/login",
                credentials,
                Object.class
        );
    }
    
    // Notification Service Routes
    
    @GetMapping("/notifications/user/{userId}")
    public ResponseEntity<Object> getNotificationsByUserId(
            @PathVariable String userId,
            @RequestHeader HttpHeaders headers) {
        return restTemplate.exchange(
                notificationServiceUrl + "/api/notifications/user/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Object.class
        );
    }
    
    // User Service Routes
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<Object> getUserProfile(
            @PathVariable String userId,
            @RequestHeader HttpHeaders headers) {
        return restTemplate.exchange(
                userServiceUrl + "/api/users/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Object.class
        );
    }
    
    @PutMapping("/users/{userId}")
    public ResponseEntity<Object> updateUserProfile(
            @PathVariable String userId,
            @RequestBody Map<String, Object> userProfile,
            @RequestHeader HttpHeaders headers) {
        return restTemplate.exchange(
                userServiceUrl + "/api/users/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(userProfile, headers),
                Object.class
        );
    }
    
    // Health Check
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("API Gateway is up and running!");
    }
}