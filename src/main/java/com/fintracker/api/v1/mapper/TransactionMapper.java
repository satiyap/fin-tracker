package com.fintracker.api.v1.mapper;

import com.fintracker.api.v1.dto.TransactionDTO;
import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.Category;
import com.fintracker.core.domain.ScheduledTransaction;
import com.fintracker.core.domain.Transaction;
import com.fintracker.core.domain.User;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    
    public TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        return TransactionDTO.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .transactionType(transaction.getTransactionType())
                .accountId(transaction.getAccount() != null ? transaction.getAccount().getId() : null)
                .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
                .createdById(transaction.getCreatedBy() != null ? transaction.getCreatedBy().getId() : null)
                .scheduledTransactionId(transaction.getScheduledTransaction() != null ? transaction.getScheduledTransaction().getId() : null)
                .notes(transaction.getNotes())
                .build();
    }
    
    public Transaction toEntity(TransactionDTO transactionDTO) {
        if (transactionDTO == null) {
            return null;
        }
        
        Transaction transaction = Transaction.builder()
                .id(transactionDTO.getId())
                .description(transactionDTO.getDescription())
                .amount(transactionDTO.getAmount())
                .transactionDate(transactionDTO.getTransactionDate())
                .transactionType(transactionDTO.getTransactionType())
                .notes(transactionDTO.getNotes())
                .build();
        
        if (transactionDTO.getAccountId() != null) {
            Account account = new Account();
            account.setId(transactionDTO.getAccountId());
            transaction.setAccount(account);
        }
        
        if (transactionDTO.getCategoryId() != null) {
            Category category = new Category();
            category.setId(transactionDTO.getCategoryId());
            transaction.setCategory(category);
        }
        
        if (transactionDTO.getCreatedById() != null) {
            User user = new User();
            user.setId(transactionDTO.getCreatedById());
            transaction.setCreatedBy(user);
        }
        
        if (transactionDTO.getScheduledTransactionId() != null) {
            ScheduledTransaction scheduledTransaction = new ScheduledTransaction();
            scheduledTransaction.setId(transactionDTO.getScheduledTransactionId());
            transaction.setScheduledTransaction(scheduledTransaction);
        }
        
        return transaction;
    }
}