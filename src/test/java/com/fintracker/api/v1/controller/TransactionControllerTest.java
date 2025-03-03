package com.fintracker.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintracker.api.v1.dto.TransactionDTO;
import com.fintracker.api.v1.mapper.TransactionMapper;
import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.Category;
import com.fintracker.core.domain.Transaction;
import com.fintracker.core.domain.User;
import com.fintracker.core.service.TransactionService;
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

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private TransactionMapper transactionMapper;

    private User user;
    private Account account;
    private Category category;
    private Transaction transaction;
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

        transactionDTO = TransactionDTO.builder()
                .id(1L)
                .description("Test Transaction")
                .amount(new BigDecimal("100.00"))
                .transactionDate(now)
                .transactionType("EXPENSE")
                .accountId(1L)
                .categoryId(1L)
                .createdById(1L)
                .notes("Test notes")
                .build();
    }

    @Test
    @WithMockUser
    void getAllTransactions_ShouldReturnAllTransactions() throws Exception {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getAllTransactions()).thenReturn(transactions);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Transaction")))
                .andExpect(jsonPath("$[0].amount", is(100.00)))
                .andExpect(jsonPath("$[0].transactionType", is("EXPENSE")))
                .andExpect(jsonPath("$[0].accountId", is(1)))
                .andExpect(jsonPath("$[0].categoryId", is(1)))
                .andExpect(jsonPath("$[0].createdById", is(1)));

        verify(transactionService, times(1)).getAllTransactions();
        verify(transactionMapper, times(1)).toDTO(transaction);
    }

    @Test
    @WithMockUser
    void getTransactionById_WithValidId_ShouldReturnTransaction() throws Exception {
        // Arrange
        when(transactionService.getTransactionById(1L)).thenReturn(transaction);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test Transaction")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.transactionType", is("EXPENSE")))
                .andExpect(jsonPath("$.accountId", is(1)))
                .andExpect(jsonPath("$.categoryId", is(1)))
                .andExpect(jsonPath("$.createdById", is(1)));

        verify(transactionService, times(1)).getTransactionById(1L);
        verify(transactionMapper, times(1)).toDTO(transaction);
    }

    @Test
    @WithMockUser
    void getTransactionsByAccountId_WithValidAccountId_ShouldReturnTransactions() throws Exception {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getTransactionsByAccountId(1L)).thenReturn(transactions);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Transaction")))
                .andExpect(jsonPath("$[0].accountId", is(1)));

        verify(transactionService, times(1)).getTransactionsByAccountId(1L);
        verify(transactionMapper, times(1)).toDTO(transaction);
    }

    @Test
    @WithMockUser
    void getTransactionsByCategoryId_WithValidCategoryId_ShouldReturnTransactions() throws Exception {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getTransactionsByCategoryId(1L)).thenReturn(transactions);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions/category/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Transaction")))
                .andExpect(jsonPath("$[0].categoryId", is(1)));

        verify(transactionService, times(1)).getTransactionsByCategoryId(1L);
        verify(transactionMapper, times(1)).toDTO(transaction);
    }

    @Test
    @WithMockUser
    void getTransactionsByUserId_WithValidUserId_ShouldReturnTransactions() throws Exception {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getTransactionsByUserId(1L)).thenReturn(transactions);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Transaction")))
                .andExpect(jsonPath("$[0].createdById", is(1)));

        verify(transactionService, times(1)).getTransactionsByUserId(1L);
        verify(transactionMapper, times(1)).toDTO(transaction);
    }

    @Test
    @WithMockUser
    void getTransactionsByDateRange_WithValidDateRange_ShouldReturnTransactions() throws Exception {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction);
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String startStr = start.format(formatter);
        String endStr = end.format(formatter);

        when(transactionService.getTransactionsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(transactions);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions/date-range")
                .param("start", startStr)
                .param("end", endStr))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Transaction")));

        verify(transactionService, times(1)).getTransactionsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(transactionMapper, times(1)).toDTO(transaction);
    }

    @Test
    @WithMockUser
    void createTransaction_WithValidTransaction_ShouldReturnCreatedTransaction() throws Exception {
        // Arrange
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
        when(transactionService.createTransaction(transaction, 1L, 1L, 1L)).thenReturn(transaction);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/transactions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test Transaction")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.transactionType", is("EXPENSE")))
                .andExpect(jsonPath("$.accountId", is(1)))
                .andExpect(jsonPath("$.categoryId", is(1)))
                .andExpect(jsonPath("$.createdById", is(1)));

        verify(transactionMapper, times(1)).toEntity(transactionDTO);
        verify(transactionService, times(1)).createTransaction(transaction, 1L, 1L, 1L);
        verify(transactionMapper, times(1)).toDTO(transaction);
    }

    @Test
    @WithMockUser
    void updateTransaction_WithValidTransaction_ShouldReturnUpdatedTransaction() throws Exception {
        // Arrange
        when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
        when(transactionService.updateTransaction(eq(1L), any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/transactions/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test Transaction")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.transactionType", is("EXPENSE")))
                .andExpect(jsonPath("$.accountId", is(1)))
                .andExpect(jsonPath("$.categoryId", is(1)))
                .andExpect(jsonPath("$.createdById", is(1)));

        verify(transactionMapper, times(1)).toEntity(transactionDTO);
        verify(transactionService, times(1)).updateTransaction(eq(1L), any(Transaction.class));
        verify(transactionMapper, times(1)).toDTO(transaction);
    }

    @Test
    @WithMockUser
    void deleteTransaction_WithValidId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(transactionService).deleteTransaction(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/transactions/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(transactionService, times(1)).deleteTransaction(1L);
    }
}