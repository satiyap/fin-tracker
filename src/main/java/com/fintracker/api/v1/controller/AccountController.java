package com.fintracker.api.v1.controller;

import com.fintracker.api.v1.dto.AccountDTO;
import com.fintracker.api.v1.mapper.AccountMapper;
import com.fintracker.core.domain.Account;
import com.fintracker.core.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management API")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @GetMapping
    @Operation(summary = "Get all accounts", description = "Get a list of all accounts")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        List<AccountDTO> accountDTOs = accounts.stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accountDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Get account details by ID")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(accountMapper.toDTO(account));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get accounts by user ID", description = "Get a list of accounts for a specific user")
    public ResponseEntity<List<AccountDTO>> getAccountsByUserId(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        List<AccountDTO> accountDTOs = accounts.stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accountDTOs);
    }

    @PostMapping
    @Operation(summary = "Create account", description = "Create a new account")
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        Account account = accountMapper.toEntity(accountDTO);
        Account savedAccount = accountService.createAccount(account, accountDTO.getUserId());
        return ResponseEntity.ok(accountMapper.toDTO(savedAccount));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account", description = "Update an existing account")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountDTO accountDTO) {
        Account account = accountMapper.toEntity(accountDTO);
        Account updatedAccount = accountService.updateAccount(id, account);
        return ResponseEntity.ok(accountMapper.toDTO(updatedAccount));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account", description = "Delete an account by ID")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}