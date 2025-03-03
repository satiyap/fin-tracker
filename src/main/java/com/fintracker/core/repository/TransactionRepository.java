package com.fintracker.core.repository;

import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.Category;
import com.fintracker.core.domain.Transaction;
import com.fintracker.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount(Account account);
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByCategory(Category category);
    List<Transaction> findByCategoryId(Long categoryId);
    List<Transaction> findByCreatedBy(User user);
    List<Transaction> findByCreatedById(Long userId);
    List<Transaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.transactionDate BETWEEN :start AND :end")
    List<Transaction> findByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.transactionDate BETWEEN :start AND :end")
    List<Transaction> findByAccountIdAndDateRange(Long accountId, LocalDateTime start, LocalDateTime end);
}