package com.bankapp.transaction.service;

import com.bankapp.transaction.model.Transaction;
import com.bankapp.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${account.service.url}")
    private String accountServiceUrl;
    
    @Value("${notification.service.url}")
    private String notificationServiceUrl;
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }
    
    public Transaction getTransactionByTransactionId(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with transactionId: " + transactionId));
    }
    
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
        List<Transaction> sourceTransactions = transactionRepository.findBySourceAccountNumber(accountNumber);
        List<Transaction> destinationTransactions = transactionRepository.findByDestinationAccountNumber(accountNumber);
        
        sourceTransactions.addAll(destinationTransactions);
        return sourceTransactions;
    }
    
    @Transactional
    public Transaction createDeposit(String accountNumber, BigDecimal amount, String description, Long userId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransactionId());
        transaction.setSourceAccountNumber(accountNumber);
        transaction.setDestinationAccountNumber(accountNumber);
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setDescription(description);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUserId(userId);
        
        transaction = transactionRepository.save(transaction);
        
        try {
            // Call account service to update balance
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, BigDecimal> requestMap = new HashMap<>();
            requestMap.put("amount", amount);
            
            HttpEntity<Map<String, BigDecimal>> request = new HttpEntity<>(requestMap, headers);
            
            restTemplate.postForObject(
                    accountServiceUrl + "/api/accounts/" + accountNumber + "/deposit",
                    request,
                    Object.class);
            
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            transaction = transactionRepository.save(transaction);
            
            // Notify user about the deposit
            sendNotification(userId, "Deposit of " + amount + " successful to account " + accountNumber);
            
            return transaction;
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Failed to process deposit: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public Transaction createWithdrawal(String accountNumber, BigDecimal amount, String description, Long userId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransactionId());
        transaction.setSourceAccountNumber(accountNumber);
        transaction.setDestinationAccountNumber(accountNumber);
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.WITHDRAWAL);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setDescription(description);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUserId(userId);
        
        transaction = transactionRepository.save(transaction);
        
        try {
            // Call account service to update balance
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, BigDecimal> requestMap = new HashMap<>();
            requestMap.put("amount", amount);
            
            HttpEntity<Map<String, BigDecimal>> request = new HttpEntity<>(requestMap, headers);
            
            restTemplate.postForObject(
                    accountServiceUrl + "/api/accounts/" + accountNumber + "/withdraw",
                    request,
                    Object.class);
            
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            transaction = transactionRepository.save(transaction);
            
            // Notify user about the withdrawal
            sendNotification(userId, "Withdrawal of " + amount + " successful from account " + accountNumber);
            
            return transaction;
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Failed to process withdrawal: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public Transaction createTransfer(String sourceAccountNumber, String destinationAccountNumber, 
                               BigDecimal amount, String description, Long userId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransactionId());
        transaction.setSourceAccountNumber(sourceAccountNumber);
        transaction.setDestinationAccountNumber(destinationAccountNumber);
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setDescription(description);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUserId(userId);
        
        transaction = transactionRepository.save(transaction);
        
        try {
            // Call account service to withdraw from source account
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, BigDecimal> requestMap = new HashMap<>();
            requestMap.put("amount", amount);
            
            HttpEntity<Map<String, BigDecimal>> request = new HttpEntity<>(requestMap, headers);
            
            restTemplate.postForObject(
                    accountServiceUrl + "/api/accounts/" + sourceAccountNumber + "/withdraw",
                    request,
                    Object.class);
            
            // Call account service to deposit to destination account
            restTemplate.postForObject(
                    accountServiceUrl + "/api/accounts/" + destinationAccountNumber + "/deposit",
                    request,
                    Object.class);
            
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            transaction = transactionRepository.save(transaction);
            
            // Notify user about the transfer
            sendNotification(userId, "Transfer of " + amount + " successful from account " + 
                           sourceAccountNumber + " to account " + destinationAccountNumber);
            
            return transaction;
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Failed to process transfer: " + e.getMessage(), e);
        }
    }
    
    private String generateTransactionId() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
    }
    
    private void sendNotification(Long userId, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> notification = new HashMap<>();
            notification.put("userId", userId);
            notification.put("message", message);
            notification.put("type", "TRANSACTION");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(notification, headers);
            
            restTemplate.postForObject(
                    notificationServiceUrl + "/api/notifications",
                    request,
                    Object.class);
        } catch (Exception e) {
            // Log the error but don't fail the transaction
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
}