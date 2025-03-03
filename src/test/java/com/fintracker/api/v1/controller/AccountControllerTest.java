package com.fintracker.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintracker.api.v1.dto.AccountDTO;
import com.fintracker.api.v1.mapper.AccountMapper;
import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.User;
import com.fintracker.core.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountMapper accountMapper;

    private Account account;
    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        User user = User.builder()
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

        accountDTO = AccountDTO.builder()
                .id(1L)
                .name("Test Account")
                .accountType("SAVINGS")
                .balance(new BigDecimal("1000.00"))
                .userId(1L)
                .build();
    }

    @Test
    @WithMockUser
    void getAllAccounts_ShouldReturnAllAccounts() throws Exception {
        // Arrange
        List<Account> accounts = Arrays.asList(account);
        when(accountService.getAllAccounts()).thenReturn(accounts);
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Account")))
                .andExpect(jsonPath("$[0].accountType", is("SAVINGS")))
                .andExpect(jsonPath("$[0].balance", is(1000.00)))
                .andExpect(jsonPath("$[0].userId", is(1)));

        verify(accountService, times(1)).getAllAccounts();
        verify(accountMapper, times(1)).toDTO(account);
    }

    @Test
    @WithMockUser
    void getAccountById_WithValidId_ShouldReturnAccount() throws Exception {
        // Arrange
        when(accountService.getAccountById(1L)).thenReturn(account);
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Account")))
                .andExpect(jsonPath("$.accountType", is("SAVINGS")))
                .andExpect(jsonPath("$.balance", is(1000.00)))
                .andExpect(jsonPath("$.userId", is(1)));

        verify(accountService, times(1)).getAccountById(1L);
        verify(accountMapper, times(1)).toDTO(account);
    }

    @Test
    @WithMockUser
    void getAccountsByUserId_WithValidUserId_ShouldReturnAccounts() throws Exception {
        // Arrange
        List<Account> accounts = Arrays.asList(account);
        when(accountService.getAccountsByUserId(1L)).thenReturn(accounts);
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Account")))
                .andExpect(jsonPath("$[0].accountType", is("SAVINGS")))
                .andExpect(jsonPath("$[0].balance", is(1000.00)))
                .andExpect(jsonPath("$[0].userId", is(1)));

        verify(accountService, times(1)).getAccountsByUserId(1L);
        verify(accountMapper, times(1)).toDTO(account);
    }

    @Test
    @WithMockUser
    void createAccount_WithValidAccount_ShouldReturnCreatedAccount() throws Exception {
        // Arrange
        when(accountMapper.toEntity(accountDTO)).thenReturn(account);
        when(accountService.createAccount(account, 1L)).thenReturn(account);
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Account")))
                .andExpect(jsonPath("$.accountType", is("SAVINGS")))
                .andExpect(jsonPath("$.balance", is(1000.00)))
                .andExpect(jsonPath("$.userId", is(1)));

        verify(accountMapper, times(1)).toEntity(accountDTO);
        verify(accountService, times(1)).createAccount(account, 1L);
        verify(accountMapper, times(1)).toDTO(account);
    }

    @Test
    @WithMockUser
    void updateAccount_WithValidAccount_ShouldReturnUpdatedAccount() throws Exception {
        // Arrange
        when(accountMapper.toEntity(accountDTO)).thenReturn(account);
        when(accountService.updateAccount(eq(1L), any(Account.class))).thenReturn(account);
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/accounts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Account")))
                .andExpect(jsonPath("$.accountType", is("SAVINGS")))
                .andExpect(jsonPath("$.balance", is(1000.00)))
                .andExpect(jsonPath("$.userId", is(1)));

        verify(accountMapper, times(1)).toEntity(accountDTO);
        verify(accountService, times(1)).updateAccount(eq(1L), any(Account.class));
        verify(accountMapper, times(1)).toDTO(account);
    }

    @Test
    @WithMockUser
    void deleteAccount_WithValidId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(accountService).deleteAccount(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/accounts/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(accountService, times(1)).deleteAccount(1L);
    }
}