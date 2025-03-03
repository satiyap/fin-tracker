package com.fintracker.core.repository;

import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.Category;
import com.fintracker.core.domain.ScheduledTransaction;
import com.fintracker.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledTransactionRepository extends JpaRepository<ScheduledTransaction, Long> {
    List<ScheduledTransaction> findByAccount(Account account);
    List<ScheduledTransaction> findByAccountId(Long accountId);
    List<ScheduledTransaction> findByCategory(Category category);
    List<ScheduledTransaction> findByCategoryId(Long categoryId);
    List<ScheduledTransaction> findByCreatedBy(User user);
    List<ScheduledTransaction> findByCreatedById(Long userId);
    List<ScheduledTransaction> findByNextDueDateBefore(LocalDateTime date);
    List<ScheduledTransaction> findByActiveTrue();
    List<ScheduledTransaction> findByActiveTrueAndNextDueDateBefore(LocalDateTime date);
}