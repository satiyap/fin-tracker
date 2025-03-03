package com.fintracker.core.service;

import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.Category;
import com.fintracker.core.domain.Transaction;
import com.fintracker.core.domain.User;
import com.fintracker.core.exception.ResourceNotFoundException;
import com.fintracker.core.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCategoryId(Long categoryId) {
        return transactionRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByCreatedById(userId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByTransactionDateBetween(start, end);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByUserIdAndDateRange(userId, start, end);
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction, Long accountId, Long categoryId, Long userId) {
        Account account = accountService.getAccountById(accountId);
        Category category = categoryService.getCategoryById(categoryId);
        User user = userService.getUserById(userId);
        
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setCreatedBy(user);
        
        // Update account balance
        updateAccountBalance(account, transaction.getAmount(), transaction.getTransactionType());
        
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = getTransactionById(id);
        
        // Revert the old transaction's effect on account balance
        updateAccountBalance(transaction.getAccount(), transaction.getAmount().negate(), transaction.getTransactionType());
        
        transaction.setDescription(transactionDetails.getDescription());
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setTransactionDate(transactionDetails.getTransactionDate());
        transaction.setTransactionType(transactionDetails.getTransactionType());
        transaction.setNotes(transactionDetails.getNotes());
        
        if (transactionDetails.getAccount() != null && !transaction.getAccount().getId().equals(transactionDetails.getAccount().getId())) {
            Account newAccount = accountService.getAccountById(transactionDetails.getAccount().getId());
            transaction.setAccount(newAccount);
        }
        
        if (transactionDetails.getCategory() != null && !transaction.getCategory().getId().equals(transactionDetails.getCategory().getId())) {
            Category newCategory = categoryService.getCategoryById(transactionDetails.getCategory().getId());
            transaction.setCategory(newCategory);
        }
        
        // Apply the new transaction's effect on account balance
        updateAccountBalance(transaction.getAccount(), transaction.getAmount(), transaction.getTransactionType());
        
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = getTransactionById(id);
        
        // Revert the transaction's effect on account balance
        updateAccountBalance(transaction.getAccount(), transaction.getAmount().negate(), transaction.getTransactionType());
        
        transactionRepository.delete(transaction);
    }
    
    private void updateAccountBalance(Account account, BigDecimal amount, String transactionType) {
        if ("EXPENSE".equals(transactionType)) {
            account.setBalance(account.getBalance().subtract(amount));
        } else if ("INCOME".equals(transactionType)) {
            account.setBalance(account.getBalance().add(amount));
        }
        // For TRANSFER, the balance update would be handled differently
        
        accountService.updateAccount(account.getId(), account);
    }
}