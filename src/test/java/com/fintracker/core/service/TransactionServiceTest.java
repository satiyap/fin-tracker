package com.fintracker.core.service;

import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.Category;
import com.fintracker.core.domain.Transaction;
import com.fintracker.core.domain.User;
import com.fintracker.core.exception.ResourceNotFoundException;
import com.fintracker.core.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Account account;
    private Category category;
    private Transaction transaction;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        account = Account.builder()
                .id(1L)
                .name("Test Account")
                .accountType("SAVINGS")
                .balance(new BigDecimal("1000.00"))
                .user(user)
                .build();

        category = Category.builder()
                .id(1L)
                .name("Test Category")
                .type("EXPENSE")
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .description("Test Transaction")
                .amount(new BigDecimal("100.00"))
                .transactionDate(now)
                .transactionType("EXPENSE")
                .account(account)
                .category(category)
                .createdBy(user)
                .notes("Test notes")
                .build();
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getAllTransactions();

        // Assert
        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void getTransactionById_WithValidId_ShouldReturnTransaction() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // Act
        Transaction result = transactionService.getTransactionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Transaction", result.getDescription());
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void getTransactionById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(999L));
        verify(transactionRepository, times(1)).findById(999L);
    }

    @Test
    void getTransactionsByAccountId_ShouldReturnTransactionsForAccount() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionRepository.findByAccountId(1L)).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByAccountId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Transaction", result.get(0).getDescription());
        verify(transactionRepository, times(1)).findByAccountId(1L);
    }

    @Test
    void getTransactionsByCategoryId_ShouldReturnTransactionsForCategory() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionRepository.findByCategoryId(1L)).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByCategoryId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Transaction", result.get(0).getDescription());
        verify(transactionRepository, times(1)).findByCategoryId(1L);
    }

    @Test
    void getTransactionsByUserId_ShouldReturnTransactionsForUser() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionRepository.findByCreatedById(1L)).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByUserId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Transaction", result.get(0).getDescription());
        verify(transactionRepository, times(1)).findByCreatedById(1L);
    }

    @Test
    void getTransactionsByDateRange_ShouldReturnTransactionsInRange() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction);
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);
        when(transactionRepository.findByTransactionDateBetween(start, end)).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByDateRange(start, end);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Transaction", result.get(0).getDescription());
        verify(transactionRepository, times(1)).findByTransactionDateBetween(start, end);
    }

    @Test
    void createTransaction_WithValidTransaction_ShouldReturnSavedTransaction() {
        // Arrange
        when(accountService.getAccountById(1L)).thenReturn(account);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(userService.getUserById(1L)).thenReturn(user);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction result = transactionService.createTransaction(transaction, 1L, 1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Transaction", result.getDescription());
        verify(accountService, times(1)).getAccountById(1L);
        verify(categoryService, times(1)).getCategoryById(1L);
        verify(userService, times(1)).getUserById(1L);
        verify(accountService, times(1)).updateAccount(eq(1L), any(Account.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_WithValidTransaction_ShouldReturnUpdatedTransaction() {
        // Arrange
        Transaction updatedTransaction = Transaction.builder()
                .id(1L)
                .description("Updated Transaction")
                .amount(new BigDecimal("200.00"))
                .transactionDate(now)
                .transactionType("EXPENSE")
                .account(account)
                .category(category)
                .createdBy(user)
                .notes("Updated notes")
                .build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(updatedTransaction);

        // Act
        Transaction result = transactionService.updateTransaction(1L, updatedTransaction);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Transaction", result.getDescription());
        assertEquals(new BigDecimal("200.00"), result.getAmount());
        verify(transactionRepository, times(1)).findById(1L);
        verify(accountService, times(2)).updateAccount(eq(1L), any(Account.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void deleteTransaction_WithValidId_ShouldDeleteTransaction() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        doNothing().when(transactionRepository).delete(transaction);

        // Act
        transactionService.deleteTransaction(1L);

        // Assert
        verify(transactionRepository, times(1)).findById(1L);
        verify(accountService, times(1)).updateAccount(eq(1L), any(Account.class));
        verify(transactionRepository, times(1)).delete(transaction);
    }
}