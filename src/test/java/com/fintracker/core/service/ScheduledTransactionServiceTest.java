package com.fintracker.core.service;

import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.Category;
import com.fintracker.core.domain.ScheduledTransaction;
import com.fintracker.core.domain.Transaction;
import com.fintracker.core.domain.User;
import com.fintracker.core.exception.ResourceNotFoundException;
import com.fintracker.core.repository.ScheduledTransactionRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledTransactionServiceTest {

    @Mock
    private ScheduledTransactionRepository scheduledTransactionRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ScheduledTransactionService scheduledTransactionService;

    private User user;
    private Account account;
    private Category category;
    private ScheduledTransaction scheduledTransaction;
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

        scheduledTransaction = ScheduledTransaction.builder()
                .id(1L)
                .description("Test Scheduled Transaction")
                .amount(new BigDecimal("100.00"))
                .frequency("MONTHLY")
                .nextDueDate(now.plusDays(7))
                .transactionType("EXPENSE")
                .account(account)
                .category(category)
                .createdBy(user)
                .notes("Test notes")
                .active(true)
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
                .scheduledTransaction(scheduledTransaction)
                .notes("Test notes")
                .build();
    }

    @Test
    void getAllScheduledTransactions_ShouldReturnAllScheduledTransactions() {
        // Arrange
        List<ScheduledTransaction> scheduledTransactions = Arrays.asList(scheduledTransaction);
        when(scheduledTransactionRepository.findAll()).thenReturn(scheduledTransactions);

        // Act
        List<ScheduledTransaction> result = scheduledTransactionService.getAllScheduledTransactions();

        // Assert
        assertEquals(1, result.size());
        verify(scheduledTransactionRepository, times(1)).findAll();
    }

    @Test
    void getScheduledTransactionById_WithValidId_ShouldReturnScheduledTransaction() {
        // Arrange
        when(scheduledTransactionRepository.findById(1L)).thenReturn(Optional.of(scheduledTransaction));

        // Act
        ScheduledTransaction result = scheduledTransactionService.getScheduledTransactionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Scheduled Transaction", result.getDescription());
        verify(scheduledTransactionRepository, times(1)).findById(1L);
    }

    @Test
    void getScheduledTransactionById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(scheduledTransactionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> scheduledTransactionService.getScheduledTransactionById(999L));
        verify(scheduledTransactionRepository, times(1)).findById(999L);
    }

    @Test
    void getScheduledTransactionsByAccountId_ShouldReturnScheduledTransactionsForAccount() {
        // Arrange
        List<ScheduledTransaction> scheduledTransactions = Arrays.asList(scheduledTransaction);
        when(scheduledTransactionRepository.findByAccountId(1L)).thenReturn(scheduledTransactions);

        // Act
        List<ScheduledTransaction> result = scheduledTransactionService.getScheduledTransactionsByAccountId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Scheduled Transaction", result.get(0).getDescription());
        verify(scheduledTransactionRepository, times(1)).findByAccountId(1L);
    }

    @Test
    void getScheduledTransactionsByCategoryId_ShouldReturnScheduledTransactionsForCategory() {
        // Arrange
        List<ScheduledTransaction> scheduledTransactions = Arrays.asList(scheduledTransaction);
        when(scheduledTransactionRepository.findByCategoryId(1L)).thenReturn(scheduledTransactions);

        // Act
        List<ScheduledTransaction> result = scheduledTransactionService.getScheduledTransactionsByCategoryId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Scheduled Transaction", result.get(0).getDescription());
        verify(scheduledTransactionRepository, times(1)).findByCategoryId(1L);
    }

    @Test
    void getScheduledTransactionsByUserId_ShouldReturnScheduledTransactionsForUser() {
        // Arrange
        List<ScheduledTransaction> scheduledTransactions = Arrays.asList(scheduledTransaction);
        when(scheduledTransactionRepository.findByCreatedById(1L)).thenReturn(scheduledTransactions);

        // Act
        List<ScheduledTransaction> result = scheduledTransactionService.getScheduledTransactionsByUserId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Scheduled Transaction", result.get(0).getDescription());
        verify(scheduledTransactionRepository, times(1)).findByCreatedById(1L);
    }

    @Test
    void getUpcomingScheduledTransactions_ShouldReturnUpcomingScheduledTransactions() {
        // Arrange
        List<ScheduledTransaction> scheduledTransactions = Arrays.asList(scheduledTransaction);
        LocalDateTime date = now.plusDays(10);
        when(scheduledTransactionRepository.findByNextDueDateBefore(date)).thenReturn(scheduledTransactions);

        // Act
        List<ScheduledTransaction> result = scheduledTransactionService.getUpcomingScheduledTransactions(date);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Scheduled Transaction", result.get(0).getDescription());
        verify(scheduledTransactionRepository, times(1)).findByNextDueDateBefore(date);
    }

    @Test
    void createScheduledTransaction_WithValidScheduledTransaction_ShouldReturnSavedScheduledTransaction() {
        // Arrange
        when(accountService.getAccountById(1L)).thenReturn(account);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(userService.getUserById(1L)).thenReturn(user);
        when(scheduledTransactionRepository.save(any(ScheduledTransaction.class))).thenReturn(scheduledTransaction);

        // Act
        ScheduledTransaction result = scheduledTransactionService.createScheduledTransaction(scheduledTransaction, 1L, 1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Scheduled Transaction", result.getDescription());
        assertTrue(result.isActive());
        verify(accountService, times(1)).getAccountById(1L);
        verify(categoryService, times(1)).getCategoryById(1L);
        verify(userService, times(1)).getUserById(1L);
        verify(scheduledTransactionRepository, times(1)).save(any(ScheduledTransaction.class));
    }

    @Test
    void updateScheduledTransaction_WithValidScheduledTransaction_ShouldReturnUpdatedScheduledTransaction() {
        // Arrange
        ScheduledTransaction updatedScheduledTransaction = ScheduledTransaction.builder()
                .id(1L)
                .description("Updated Scheduled Transaction")
                .amount(new BigDecimal("200.00"))
                .frequency("WEEKLY")
                .nextDueDate(now.plusDays(3))
                .transactionType("EXPENSE")
                .account(account)
                .category(category)
                .createdBy(user)
                .notes("Updated notes")
                .active(true)
                .build();

        when(scheduledTransactionRepository.findById(1L)).thenReturn(Optional.of(scheduledTransaction));
        when(scheduledTransactionRepository.save(any(ScheduledTransaction.class))).thenReturn(updatedScheduledTransaction);

        // Act
        ScheduledTransaction result = scheduledTransactionService.updateScheduledTransaction(1L, updatedScheduledTransaction);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Scheduled Transaction", result.getDescription());
        assertEquals(new BigDecimal("200.00"), result.getAmount());
        assertEquals("WEEKLY", result.getFrequency());
        verify(scheduledTransactionRepository, times(1)).findById(1L);
        verify(scheduledTransactionRepository, times(1)).save(any(ScheduledTransaction.class));
    }

    @Test
    void deleteScheduledTransaction_WithValidId_ShouldDeleteScheduledTransaction() {
        // Arrange
        when(scheduledTransactionRepository.findById(1L)).thenReturn(Optional.of(scheduledTransaction));
        doNothing().when(scheduledTransactionRepository).delete(scheduledTransaction);

        // Act
        scheduledTransactionService.deleteScheduledTransaction(1L);

        // Assert
        verify(scheduledTransactionRepository, times(1)).findById(1L);
        verify(scheduledTransactionRepository, times(1)).delete(scheduledTransaction);
    }

    @Test
    void executeScheduledTransaction_WithValidId_ShouldCreateTransactionAndUpdateNextDueDate() {
        // Arrange
        when(scheduledTransactionRepository.findById(1L)).thenReturn(Optional.of(scheduledTransaction));
        when(transactionService.createTransaction(any(Transaction.class), eq(1L), eq(1L), eq(1L))).thenReturn(transaction);
        when(scheduledTransactionRepository.save(any(ScheduledTransaction.class))).thenReturn(scheduledTransaction);

        // Act
        Transaction result = scheduledTransactionService.executeScheduledTransaction(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Transaction", result.getDescription());
        verify(scheduledTransactionRepository, times(1)).findById(1L);
        verify(transactionService, times(1)).createTransaction(any(Transaction.class), eq(1L), eq(1L), eq(1L));
        verify(scheduledTransactionRepository, times(1)).save(any(ScheduledTransaction.class));
    }

    @Test
    void processScheduledTransactions_ShouldExecuteAllDueTransactions() {
        // Arrange
        List<ScheduledTransaction> dueTransactions = Arrays.asList(scheduledTransaction);
        when(scheduledTransactionRepository.findByActiveTrueAndNextDueDateBefore(any(LocalDateTime.class)))
                .thenReturn(dueTransactions);
        when(scheduledTransactionRepository.findById(1L)).thenReturn(Optional.of(scheduledTransaction));
        when(transactionService.createTransaction(any(Transaction.class), eq(1L), eq(1L), eq(1L))).thenReturn(transaction);
        when(scheduledTransactionRepository.save(any(ScheduledTransaction.class))).thenReturn(scheduledTransaction);

        // Act
        scheduledTransactionService.processScheduledTransactions();

        // Assert
        verify(scheduledTransactionRepository, times(1)).findByActiveTrueAndNextDueDateBefore(any(LocalDateTime.class));
        verify(scheduledTransactionRepository, times(1)).findById(1L);
        verify(transactionService, times(1)).createTransaction(any(Transaction.class), eq(1L), eq(1L), eq(1L));
        verify(scheduledTransactionRepository, times(1)).save(any(ScheduledTransaction.class));
    }
}