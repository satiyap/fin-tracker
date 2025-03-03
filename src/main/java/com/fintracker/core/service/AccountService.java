package com.fintracker.core.service;

import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.User;
import com.fintracker.core.exception.ResourceNotFoundException;
import com.fintracker.core.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @Transactional
    public Account createAccount(Account account, Long userId) {
        User user = userService.getUserById(userId);
        account.setUser(user);
        return accountRepository.save(account);
    }

    @Transactional
    public Account updateAccount(Long id, Account accountDetails) {
        Account account = getAccountById(id);
        
        account.setName(accountDetails.getName());
        account.setAccountType(accountDetails.getAccountType());
        account.setBalance(accountDetails.getBalance());
        
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account account = getAccountById(id);
        accountRepository.delete(account);
    }
}