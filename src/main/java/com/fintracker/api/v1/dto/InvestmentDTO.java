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
public class InvestmentDTO {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Investment type is required")
    private String investmentType;
    
    @NotNull(message = "Initial amount is required")
    @Positive(message = "Initial amount must be positive")
    private BigDecimal initialAmount;
    
    private BigDecimal currentValue;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private BigDecimal expectedReturnRate;
    
    private Long userId;
    
    private String notes;
    
    private String ticker;
}