package com.fintracker.core.service;

import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.User;
import com.fintracker.core.exception.ResourceNotFoundException;
import com.fintracker.core.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .fullName("Test User")
                .email("test@example.com")
                .build();

        account = Account.builder()
                .id(1L)
                .name("Test Account")
                .accountType("SAVINGS")
                .balance(new BigDecimal("1000.00"))
                .user(user)
                .build();
    }

    @Test
    void getAllAccounts_ShouldReturnAllAccounts() {
        // Arrange
        List<Account> accounts = Arrays.asList(account);
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Account", result.get(0).getName());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void getAccountById_WithValidId_ShouldReturnAccount() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // Act
        Account result = accountService.getAccountById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Account", result.getName());
        assertEquals("SAVINGS", result.getAccountType());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
        assertEquals(user, result.getUser());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void getAccountById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(999L));
        verify(accountRepository, times(1)).findById(999L);
    }

    @Test
    void getAccountsByUserId_ShouldReturnAccountsForUser() {
        // Arrange
        List<Account> accounts = Arrays.asList(account);
        when(accountRepository.findByUserId(1L)).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAccountsByUserId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Account", result.get(0).getName());
        verify(accountRepository, times(1)).findByUserId(1L);
    }

    @Test
    void createAccount_WithValidAccount_ShouldReturnSavedAccount() {
        // Arrange
        Account newAccount = Account.builder()
                .name("New Account")
                .accountType("CHECKING")
                .balance(new BigDecimal("500.00"))
                .build();

        when(userService.getUserById(1L)).thenReturn(user);
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        // Act
        Account result = accountService.createAccount(newAccount, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("New Account", result.getName());
        assertEquals("CHECKING", result.getAccountType());
        assertEquals(new BigDecimal("500.00"), result.getBalance());
        verify(userService, times(1)).getUserById(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void updateAccount_WithValidAccount_ShouldReturnUpdatedAccount() {
        // Arrange
        Account updatedAccount = Account.builder()
                .id(1L)
                .name("Updated Account")
                .accountType("SAVINGS")
                .balance(new BigDecimal("1500.00"))
                .user(user)
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        // Act
        Account result = accountService.updateAccount(1L, updatedAccount);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Account", result.getName());
        assertEquals(new BigDecimal("1500.00"), result.getBalance());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void deleteAccount_WithValidId_ShouldDeleteAccount() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        doNothing().when(accountRepository).delete(account);

        // Act
        accountService.deleteAccount(1L);

        // Assert
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    void deleteAccount_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountService.deleteAccount(999L));
        verify(accountRepository, times(1)).findById(999L);
        verify(accountRepository, never()).delete(any(Account.class));
    }
}