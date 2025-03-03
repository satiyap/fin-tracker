package com.fintracker.api.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTransactionDTO {
    private Long id;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotBlank(message = "Frequency is required")
    private String frequency;
    
    @NotNull(message = "Next due date is required")
    private LocalDateTime nextDueDate;
    
    @NotBlank(message = "Transaction type is required")
    private String transactionType;
    
    @NotNull(message = "Account ID is required")
    private Long accountId;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    private Long createdById;
    
    private String notes;
    
    private boolean active;
}