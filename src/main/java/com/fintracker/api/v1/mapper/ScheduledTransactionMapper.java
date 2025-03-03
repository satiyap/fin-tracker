package com.fintracker.api.v1.mapper;

import com.fintracker.api.v1.dto.ScheduledTransactionDTO;
import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.Category;
import com.fintracker.core.domain.ScheduledTransaction;
import com.fintracker.core.domain.User;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTransactionMapper {
    
    public ScheduledTransactionDTO toDTO(ScheduledTransaction scheduledTransaction) {
        if (scheduledTransaction == null) {
            return null;
        }
        
        return ScheduledTransactionDTO.builder()
                .id(scheduledTransaction.getId())
                .description(scheduledTransaction.getDescription())
                .amount(scheduledTransaction.getAmount())
                .frequency(scheduledTransaction.getFrequency())
                .nextDueDate(scheduledTransaction.getNextDueDate())
                .transactionType(scheduledTransaction.getTransactionType())
                .accountId(scheduledTransaction.getAccount() != null ? scheduledTransaction.getAccount().getId() : null)
                .categoryId(scheduledTransaction.getCategory() != null ? scheduledTransaction.getCategory().getId() : null)
                .createdById(scheduledTransaction.getCreatedBy() != null ? scheduledTransaction.getCreatedBy().getId() : null)
                .notes(scheduledTransaction.getNotes())
                .active(scheduledTransaction.isActive())
                .build();
    }
    
    public ScheduledTransaction toEntity(ScheduledTransactionDTO scheduledTransactionDTO) {
        if (scheduledTransactionDTO == null) {
            return null;
        }
        
        ScheduledTransaction scheduledTransaction = ScheduledTransaction.builder()
                .id(scheduledTransactionDTO.getId())
                .description(scheduledTransactionDTO.getDescription())
                .amount(scheduledTransactionDTO.getAmount())
                .frequency(scheduledTransactionDTO.getFrequency())
                .nextDueDate(scheduledTransactionDTO.getNextDueDate())
                .transactionType(scheduledTransactionDTO.getTransactionType())
                .notes(scheduledTransactionDTO.getNotes())
                .active(scheduledTransactionDTO.isActive())
                .build();
        
        if (scheduledTransactionDTO.getAccountId() != null) {
            Account account = new Account();
            account.setId(scheduledTransactionDTO.getAccountId());
            scheduledTransaction.setAccount(account);
        }
        
        if (scheduledTransactionDTO.getCategoryId() != null) {
            Category category = new Category();
            category.setId(scheduledTransactionDTO.getCategoryId());
            scheduledTransaction.setCategory(category);
        }
        
        if (scheduledTransactionDTO.getCreatedById() != null) {
            User user = new User();
            user.setId(scheduledTransactionDTO.getCreatedById());
            scheduledTransaction.setCreatedBy(user);
        }
        
        return scheduledTransaction;
    }
}