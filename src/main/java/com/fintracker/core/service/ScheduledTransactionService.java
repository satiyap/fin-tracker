package com.fintracker.core.service;

import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.Category;
import com.fintracker.core.domain.ScheduledTransaction;
import com.fintracker.core.domain.Transaction;
import com.fintracker.core.domain.User;
import com.fintracker.core.exception.ResourceNotFoundException;
import com.fintracker.core.repository.ScheduledTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledTransactionService {

    private final ScheduledTransactionRepository scheduledTransactionRepository;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<ScheduledTransaction> getAllScheduledTransactions() {
        return scheduledTransactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ScheduledTransaction getScheduledTransactionById(Long id) {
        return scheduledTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduled transaction not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ScheduledTransaction> getScheduledTransactionsByAccountId(Long accountId) {
        return scheduledTransactionRepository.findByAccountId(accountId);
    }

    @Transactional(readOnly = true)
    public List<ScheduledTransaction> getScheduledTransactionsByCategoryId(Long categoryId) {
        return scheduledTransactionRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<ScheduledTransaction> getScheduledTransactionsByUserId(Long userId) {
        return scheduledTransactionRepository.findByCreatedById(userId);
    }

    @Transactional(readOnly = true)
    public List<ScheduledTransaction> getUpcomingScheduledTransactions(LocalDateTime date) {
        return scheduledTransactionRepository.findByNextDueDateBefore(date);
    }

    @Transactional
    public ScheduledTransaction createScheduledTransaction(ScheduledTransaction scheduledTransaction, 
                                                          Long accountId, Long categoryId, Long userId) {
        Account account = accountService.getAccountById(accountId);
        Category category = categoryService.getCategoryById(categoryId);
        User user = userService.getUserById(userId);
        
        scheduledTransaction.setAccount(account);
        scheduledTransaction.setCategory(category);
        scheduledTransaction.setCreatedBy(user);
        scheduledTransaction.setActive(true);
        
        return scheduledTransactionRepository.save(scheduledTransaction);
    }

    @Transactional
    public ScheduledTransaction updateScheduledTransaction(Long id, ScheduledTransaction scheduledTransactionDetails) {
        ScheduledTransaction scheduledTransaction = getScheduledTransactionById(id);
        
        scheduledTransaction.setDescription(scheduledTransactionDetails.getDescription());
        scheduledTransaction.setAmount(scheduledTransactionDetails.getAmount());
        scheduledTransaction.setFrequency(scheduledTransactionDetails.getFrequency());
        scheduledTransaction.setNextDueDate(scheduledTransactionDetails.getNextDueDate());
        scheduledTransaction.setTransactionType(scheduledTransactionDetails.getTransactionType());
        scheduledTransaction.setNotes(scheduledTransactionDetails.getNotes());
        scheduledTransaction.setActive(scheduledTransactionDetails.isActive());
        
        if (scheduledTransactionDetails.getAccount() != null && 
            !scheduledTransaction.getAccount().getId().equals(scheduledTransactionDetails.getAccount().getId())) {
            Account newAccount = accountService.getAccountById(scheduledTransactionDetails.getAccount().getId());
            scheduledTransaction.setAccount(newAccount);
        }
        
        if (scheduledTransactionDetails.getCategory() != null && 
            !scheduledTransaction.getCategory().getId().equals(scheduledTransactionDetails.getCategory().getId())) {
            Category newCategory = categoryService.getCategoryById(scheduledTransactionDetails.getCategory().getId());
            scheduledTransaction.setCategory(newCategory);
        }
        
        return scheduledTransactionRepository.save(scheduledTransaction);
    }

    @Transactional
    public void deleteScheduledTransaction(Long id) {
        ScheduledTransaction scheduledTransaction = getScheduledTransactionById(id);
        scheduledTransactionRepository.delete(scheduledTransaction);
    }

    @Transactional
    public Transaction executeScheduledTransaction(Long id) {
        ScheduledTransaction scheduledTransaction = getScheduledTransactionById(id);
        
        Transaction transaction = new Transaction();
        transaction.setDescription(scheduledTransaction.getDescription());
        transaction.setAmount(scheduledTransaction.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType(scheduledTransaction.getTransactionType());
        transaction.setNotes(scheduledTransaction.getNotes());
        transaction.setScheduledTransaction(scheduledTransaction);
        
        Transaction savedTransaction = transactionService.createTransaction(
            transaction, 
            scheduledTransaction.getAccount().getId(),
            scheduledTransaction.getCategory().getId(),
            scheduledTransaction.getCreatedBy().getId()
        );
        
        // Update next due date based on frequency
        updateNextDueDate(scheduledTransaction);
        
        return savedTransaction;
    }
    
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    @Transactional
    public void processScheduledTransactions() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledTransaction> dueTransactions = 
            scheduledTransactionRepository.findByActiveTrueAndNextDueDateBefore(now);
        
        for (ScheduledTransaction scheduledTransaction : dueTransactions) {
            executeScheduledTransaction(scheduledTransaction.getId());
        }
    }
    
    private void updateNextDueDate(ScheduledTransaction scheduledTransaction) {
        LocalDateTime nextDueDate = scheduledTransaction.getNextDueDate();
        
        switch (scheduledTransaction.getFrequency()) {
            case "DAILY":
                nextDueDate = nextDueDate.plusDays(1);
                break;
            case "WEEKLY":
                nextDueDate = nextDueDate.plusWeeks(1);
                break;
            case "MONTHLY":
                nextDueDate = nextDueDate.plusMonths(1);
                break;
            case "YEARLY":
                nextDueDate = nextDueDate.plusYears(1);
                break;
            default:
                // No change
                break;
        }
        
        scheduledTransaction.setNextDueDate(nextDueDate);
        scheduledTransactionRepository.save(scheduledTransaction);
    }
}