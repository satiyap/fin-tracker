package com.fintracker.api.v1.controller;

import com.fintracker.api.v1.dto.TransactionDTO;
import com.fintracker.api.v1.mapper.TransactionMapper;
import com.fintracker.core.domain.Transaction;
import com.fintracker.core.service.TransactionService;
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
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management API")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @GetMapping
    @Operation(summary = "Get all transactions", description = "Get a list of all transactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Get transaction details by ID")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transactionMapper.toDTO(transaction));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get transactions by account ID", description = "Get a list of transactions for a specific account")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccountId(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get transactions by category ID", description = "Get a list of transactions for a specific category")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCategoryId(@PathVariable Long categoryId) {
        List<Transaction> transactions = transactionService.getTransactionsByCategoryId(categoryId);
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get transactions by user ID", description = "Get a list of transactions created by a specific user")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByUserId(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get transactions by date range", description = "Get a list of transactions within a date range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(start, end);
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/user/{userId}/date-range")
    @Operation(summary = "Get transactions by user ID and date range", description = "Get a list of transactions for a specific user within a date range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByUserIdAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Transaction> transactions = transactionService.getTransactionsByUserIdAndDateRange(userId, start, end);
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }

    @PostMapping
    @Operation(summary = "Create transaction", description = "Create a new transaction")
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        Transaction savedTransaction = transactionService.createTransaction(
                transaction,
                transactionDTO.getAccountId(),
                transactionDTO.getCategoryId(),
                transactionDTO.getCreatedById()
        );
        return ResponseEntity.ok(transactionMapper.toDTO(savedTransaction));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update transaction", description = "Update an existing transaction")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.ok(transactionMapper.toDTO(updatedTransaction));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction", description = "Delete a transaction by ID")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}