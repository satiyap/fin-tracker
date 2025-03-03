package com.fintracker.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintracker.api.v1.dto.AuthRequest;
import com.fintracker.api.v1.dto.UserDTO;
import com.fintracker.api.v1.mapper.UserMapper;
import com.fintracker.core.domain.User;
import com.fintracker.core.service.UserService;
import com.fintracker.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Add necessary security test configuration
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserMapper userMapper;

    private User user;
    private UserDTO userDTO;
    private AuthRequest authRequest;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .fullName("Test User")
                .email("test@example.com")
                .build();

        userDTO = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .fullName("Test User")
                .email("test@example.com")
                .build();

        authRequest = AuthRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        authentication = new UsernamePasswordAuthenticationToken("testuser", "password");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateToken(authentication)).thenReturn("test-jwt-token");
        when(userService.getUserByUsername("testuser")).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf()) // Updated csrf method
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", is("test-jwt-token")))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateToken(authentication);
        verify(userService, times(1)).getUserByUsername("testuser");
    }

    @Test
    void register_WithValidUser_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userService.createUser(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .with(SecurityMockMvcRequestPostProcessors.csrf()) // Updated csrf method
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.fullName", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userMapper, times(1)).toEntity(userDTO);
        verify(userService, times(1)).createUser(user);
        verify(userMapper, times(1)).toDTO(user);
    }
}