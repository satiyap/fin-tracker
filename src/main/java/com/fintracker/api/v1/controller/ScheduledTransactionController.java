package com.fintracker.api.v1.controller;

import com.fintracker.api.v1.dto.ScheduledTransactionDTO;
import com.fintracker.api.v1.dto.TransactionDTO;
import com.fintracker.api.v1.mapper.ScheduledTransactionMapper;
import com.fintracker.api.v1.mapper.TransactionMapper;
import com.fintracker.core.domain.ScheduledTransaction;
import com.fintracker.core.domain.Transaction;
import com.fintracker.core.service.ScheduledTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/scheduled-transactions")
@RequiredArgsConstructor
@Tag(name = "Scheduled Transactions", description = "Scheduled transaction management API")
public class ScheduledTransactionController {

    private final ScheduledTransactionService scheduledTransactionService;
    private final ScheduledTransactionMapper scheduledTransactionMapper;
    private final TransactionMapper transactionMapper;

    @GetMapping
    @Operation(summary = "Get all scheduled transactions", description = "Get a list of all scheduled transactions")
    public ResponseEntity<List<ScheduledTransactionDTO>> getAllScheduledTransactions() {
        List<ScheduledTransaction> scheduledTransactions = scheduledTransactionService.getAllScheduledTransactions();
        List<ScheduledTransactionDTO> scheduledTransactionDTOs = scheduledTransactions.stream()
                .map(scheduledTransactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(scheduledTransactionDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get scheduled transaction by ID", description = "Get scheduled transaction details by ID")
    public ResponseEntity<ScheduledTransactionDTO> getScheduledTransactionById(@PathVariable Long id) {
        ScheduledTransaction scheduledTransaction = scheduledTransactionService.getScheduledTransactionById(id);
        return ResponseEntity.ok(scheduledTransactionMapper.toDTO(scheduledTransaction));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get scheduled transactions by account ID", description = "Get a list of scheduled transactions for a specific account")
    public ResponseEntity<List<ScheduledTransactionDTO>> getScheduledTransactionsByAccountId(@PathVariable Long accountId) {
        List<ScheduledTransaction> scheduledTransactions = scheduledTransactionService.getScheduledTransactionsByAccountId(accountId);
        List<ScheduledTransactionDTO> scheduledTransactionDTOs = scheduledTransactions.stream()
                .map(scheduledTransactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(scheduledTransactionDTOs);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get scheduled transactions by category ID", description = "Get a list of scheduled transactions for a specific category")
    public ResponseEntity<List<ScheduledTransactionDTO>> getScheduledTransactionsByCategoryId(@PathVariable Long categoryId) {
        List<ScheduledTransaction> scheduledTransactions = scheduledTransactionService.getScheduledTransactionsByCategoryId(categoryId);
        List<ScheduledTransactionDTO> scheduledTransactionDTOs = scheduledTransactions.stream()
                .map(scheduledTransactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(scheduledTransactionDTOs);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get scheduled transactions by user ID", description = "Get a list of scheduled transactions created by a specific user")
    public ResponseEntity<List<ScheduledTransactionDTO>> getScheduledTransactionsByUserId(@PathVariable Long userId) {
        List<ScheduledTransaction> scheduledTransactions = scheduledTransactionService.getScheduledTransactionsByUserId(userId);
        List<ScheduledTransactionDTO> scheduledTransactionDTOs = scheduledTransactions.stream()
                .map(scheduledTransactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(scheduledTransactionDTOs);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming scheduled transactions", description = "Get a list of upcoming scheduled transactions before a specific date")
    public ResponseEntity<List<ScheduledTransactionDTO>> getUpcomingScheduledTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<ScheduledTransaction> scheduledTransactions = scheduledTransactionService.getUpcomingScheduledTransactions(date);
        List<ScheduledTransactionDTO> scheduledTransactionDTOs = scheduledTransactions.stream()
                .map(scheduledTransactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(scheduledTransactionDTOs);
    }

    @PostMapping
    @Operation(summary = "Create scheduled transaction", description = "Create a new scheduled transaction")
    public ResponseEntity<ScheduledTransactionDTO> createScheduledTransaction(@Valid @RequestBody ScheduledTransactionDTO scheduledTransactionDTO) {
        ScheduledTransaction scheduledTransaction = scheduledTransactionMapper.toEntity(scheduledTransactionDTO);
        ScheduledTransaction savedScheduledTransaction = scheduledTransactionService.createScheduledTransaction(
                scheduledTransaction,
                scheduledTransactionDTO.getAccountId(),
                scheduledTransactionDTO.getCategoryId(),
                scheduledTransactionDTO.getCreatedById()
        );
        return ResponseEntity.ok(scheduledTransactionMapper.toDTO(savedScheduledTransaction));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update scheduled transaction", description = "Update an existing scheduled transaction")
    public ResponseEntity<ScheduledTransactionDTO> updateScheduledTransaction(@PathVariable Long id, @Valid @RequestBody ScheduledTransactionDTO scheduledTransactionDTO) {
        ScheduledTransaction scheduledTransaction = scheduledTransactionMapper.toEntity(scheduledTransactionDTO);
        ScheduledTransaction updatedScheduledTransaction = scheduledTransactionService.updateScheduledTransaction(id, scheduledTransaction);
        return ResponseEntity.ok(scheduledTransactionMapper.toDTO(updatedScheduledTransaction));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete scheduled transaction", description = "Delete a scheduled transaction by ID")
    public ResponseEntity<Void> deleteScheduledTransaction(@PathVariable Long id) {
        scheduledTransactionService.deleteScheduledTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/execute")
    @Operation(summary = "Execute scheduled transaction", description = "Execute a scheduled transaction and create a transaction record")
    public ResponseEntity<TransactionDTO> executeScheduledTransaction(@PathVariable Long id) {
        Transaction transaction = scheduledTransactionService.executeScheduledTransaction(id);
        return ResponseEntity.ok(transactionMapper.toDTO(transaction));
    }
}