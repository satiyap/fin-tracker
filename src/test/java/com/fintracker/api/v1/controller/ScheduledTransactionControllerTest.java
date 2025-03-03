package com.fintracker.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintracker.api.v1.dto.ScheduledTransactionDTO;
import com.fintracker.api.v1.dto.TransactionDTO;
import com.fintracker.api.v1.mapper.ScheduledTransactionMapper;
import com.fintracker.api.v1.mapper.TransactionMapper;
import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.Category;
import com.fintracker.core.domain.ScheduledTransaction;
import com.fintracker.core.domain.Transaction;
import com.fintracker.core.domain.User;
import com.fintracker.core.service.ScheduledTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduledTransactionController.class)
public class ScheduledTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScheduledTransactionService scheduledTransactionService;

    @MockBean
    private ScheduledTransactionMapper scheduledTransactionMapper;

    @MockBean
    private TransactionMapper transactionMapper;

    private User user;
    private Account account;
    private Category category;
    private ScheduledTransaction scheduledTransaction;
    private Transaction transaction;
    private ScheduledTransactionDTO scheduledTransactionDTO;
    private TransactionDTO transactionDTO;
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

        scheduledTransactionDTO = ScheduledTransactionDTO.builder()
                .id(1L)
                .description("Test Scheduled Transaction")
                .amount(new BigDecimal("100.00"))
                .frequency("MONTHLY")
                .nextDueDate(now.plusDays(7))
                .transactionType("EXPENSE")
                .accountId(1L)
                .categoryId(1L)
                .createdById(1L)
                .notes("Test notes")
                .active(true)
                .build();

        transactionDTO = TransactionDTO.builder()
                .id(1L)
                .description("Test Transaction")
                .amount(new BigDecimal("100.00"))
                .transactionDate(now)
                .transactionType("EXPENSE")
                .accountId(1L)
                .categoryId(1L)
                .createdById(1L)
                .scheduledTransactionId(1L)
                .notes("Test notes")
                .build();
    }

    @Test
    @WithMockUser
    void getAllScheduledTransactions_ShouldReturnAllScheduledTransactions() throws Exception {
        // Arrange
        List<ScheduledTransaction> scheduledTransactions = Arrays.asList(scheduledTransaction);
        when(scheduledTransactionService.getAllScheduledTransactions()).thenReturn(scheduledTransactions);
        when(scheduledTransactionMapper.toDTO(scheduledTransaction)).thenReturn(scheduledTransactionDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/scheduled-transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Scheduled Transaction")))
                .andExpect(jsonPath("$[0].amount", is(100.00)))
                .andExpect(jsonPath("$[0].frequency", is("MONTHLY")))
                .andExpect(jsonPath("$[0].transactionType", is("EXPENSE")))
                .andExpect(jsonPath("$[0].accountId", is(1)))
                .andExpect(jsonPath("$[0].categoryId", is(1)))
                .andExpect(jsonPath("$[0].createdById", is(1)))
                .andExpect(jsonPath("$[0].active", is(true)));

        verify(scheduledTransactionService, times(1)).getAllScheduledTransactions();
        verify(scheduledTransactionMapper, times(1)).toDTO(scheduledTransaction);
    }

    @Test
    @WithMockUser
    void getScheduledTransactionById_WithValidId_ShouldReturnScheduledTransaction() throws Exception {
        // Arrange
        when(scheduledTransactionService.getScheduledTransactionById(1L)).thenReturn(scheduledTransaction);
        when(scheduledTransactionMapper.toDTO(scheduledTransaction)).thenReturn(scheduledTransactionDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/scheduled-transactions/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test Scheduled Transaction")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.frequency", is("MONTHLY")))
                .andExpect(jsonPath("$.transactionType", is("EXPENSE")))
                .andExpect(jsonPath("$.accountId", is(1)))
                .andExpect(jsonPath("$.categoryId", is(1)))
                .andExpect(jsonPath("$.createdById", is(1)))
                .andExpect(jsonPath("$.active", is(true)));

        verify(scheduledTransactionService, times(1)).getScheduledTransactionById(1L);
        verify(scheduledTransactionMapper, times(1)).toDTO(scheduledTransaction);
    }

    @Test
    @WithMockUser
    void getScheduledTransactionsByAccountId_WithValidAccountId_ShouldReturnScheduledTransactions() throws Exception {
        // Arrange
        List<ScheduledTransaction> scheduledTransactions = Arrays.asList(scheduledTransaction);
        when(scheduledTransactionService.getScheduledTransactionsByAccountId(1L)).thenReturn(scheduledTransactions);
        when(scheduledTransactionMapper.toDTO(scheduledTransaction)).thenReturn(scheduledTransactionDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/scheduled-transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Scheduled Transaction")))
                .andExpect(jsonPath("$[0].accountId", is(1)));

        verify(scheduledTransactionService, times(1)).getScheduledTransactionsByAccountId(1L);
        verify(scheduledTransactionMapper, times(1)).toDTO(scheduledTransaction);
    }

    @Test
    @WithMockUser
    void getUpcomingScheduledTransactions_WithValidDate_ShouldReturnUpcomingScheduledTransactions() throws Exception {
        // Arrange
        List<ScheduledTransaction> scheduledTransactions = Arrays.asList(scheduledTransaction);
        LocalDateTime date = now.plusDays(10);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String dateStr = date.format(formatter);

        when(scheduledTransactionService.getUpcomingScheduledTransactions(any(LocalDateTime.class)))
                .thenReturn(scheduledTransactions);
        when(scheduledTransactionMapper.toDTO(scheduledTransaction)).thenReturn(scheduledTransactionDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/scheduled-transactions/upcoming")
                .param("date", dateStr))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Scheduled Transaction")));

        verify(scheduledTransactionService, times(1)).getUpcomingScheduledTransactions(any(LocalDateTime.class));
        verify(scheduledTransactionMapper, times(1)).toDTO(scheduledTransaction);
    }

    @Test
    @WithMockUser
    void createScheduledTransaction_WithValidScheduledTransaction_ShouldReturnCreatedScheduledTransaction() throws Exception {
        // Arrange
        when(scheduledTransactionMapper.toEntity(scheduledTransactionDTO)).thenReturn(scheduledTransaction);
        when(scheduledTransactionService.createScheduledTransaction(scheduledTransaction, 1L, 1L, 1L))
                .thenReturn(scheduledTransaction);
        when(scheduledTransactionMapper.toDTO(scheduledTransaction)).thenReturn(scheduledTransactionDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/scheduled-transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduledTransactionDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test Scheduled Transaction")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.frequency", is("MONTHLY")))
                .andExpect(jsonPath("$.transactionType", is("EXPENSE")))
                .andExpect(jsonPath("$.accountId", is(1)))
                .andExpect(jsonPath("$.categoryId", is(1)))
                .andExpect(jsonPath("$.createdById", is(1)))
                .andExpect(jsonPath("$.active", is(true)));

        verify(scheduledTransactionMapper, times(1)).toEntity(scheduledTransactionDTO);
        verify(scheduledTransactionService, times(1)).createScheduledTransaction(scheduledTransaction, 1L, 1L, 1L);
        verify(scheduledTransactionMapper, times(1)).toDTO(scheduledTransaction);
    }

    @Test
    @WithMockUser
    void updateScheduledTransaction_WithValidScheduledTransaction_ShouldReturnUpdatedScheduledTransaction() throws Exception {
        // Arrange
        when(scheduledTransactionMapper.toEntity(scheduledTransactionDTO)).thenReturn(scheduledTransaction);
        when(scheduledTransactionService.updateScheduledTransaction(eq(1L), any(ScheduledTransaction.class)))
                .thenReturn(scheduledTransaction);
        when(scheduledTransactionMapper.toDTO(scheduledTransaction)).thenReturn(scheduledTransactionDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/scheduled-transactions/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduledTransactionDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test Scheduled Transaction")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.frequency", is("MONTHLY")))
                .andExpect(jsonPath("$.transactionType", is("EXPENSE")))
                .andExpect(jsonPath("$.accountId", is(1)))
                .andExpect(jsonPath("$.categoryId", is(1)))
                .andExpect(jsonPath("$.createdById", is(1)))
                .andExpect(jsonPath("$.active", is(true)));

        verify(scheduledTransactionMapper, times(1)).toEntity(scheduledTransactionDTO);
        verify(scheduledTransactionService, times(1)).updateScheduledTransaction(eq(1L), any(ScheduledTransaction.class));
        verify(scheduledTransactionMapper, times(1)).toDTO(scheduledTransaction);
    }

    @Test
    @WithMockUser
    void deleteScheduledTransaction_WithValidId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(scheduledTransactionService).deleteScheduledTransaction(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/scheduled-transactions/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(scheduledTransactionService, times(1)).deleteScheduledTransaction(1L);
    }

    @Test
    @WithMockUser
    void executeScheduledTransaction_WithValidId_ShouldReturnCreatedTransaction() throws Exception {
        // Arrange
        when(scheduledTransactionService.executeScheduledTransaction(1L)).thenReturn(transaction);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/scheduled-transactions/1/execute")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test Transaction")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.transactionType", is("EXPENSE")))
                .andExpect(jsonPath("$.accountId", is(1)))
                .andExpect(jsonPath("$.categoryId", is(1)))
                .andExpect(jsonPath("$.createdById", is(1)))
                .andExpect(jsonPath("$.scheduledTransactionId", is(1)));

        verify(scheduledTransactionService, times(1)).executeScheduledTransaction(1L);
        verify(transactionMapper, times(1)).toDTO(transaction);
    }
}