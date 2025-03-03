package com.fintracker.api.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long id;
    
    @NotBlank(message = "Account name is required")
    private String name;
    
    @NotBlank(message = "Account type is required")
    private String accountType;
    
    @NotNull(message = "Balance is required")
    private BigDecimal balance;
    
    private Long userId;
}