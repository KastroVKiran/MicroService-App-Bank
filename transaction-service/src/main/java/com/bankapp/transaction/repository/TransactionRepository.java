package com.bankapp.transaction.repository;

import com.bankapp.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionId(String transactionId);
    
    List<Transaction> findBySourceAccountNumber(String sourceAccountNumber);
    
    List<Transaction> findByDestinationAccountNumber(String destinationAccountNumber);
    
    List<Transaction> findByUserId(Long userId);
    
    List<Transaction> findByType(Transaction.TransactionType type);
    
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}