package com.fintracker.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintracker.api.v1.dto.InvestmentDTO;
import com.fintracker.api.v1.mapper.InvestmentMapper;
import com.fintracker.core.domain.Investment;
import com.fintracker.core.domain.User;
import com.fintracker.core.service.InvestmentService;
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

@WebMvcTest(InvestmentController.class)
public class InvestmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InvestmentService investmentService;

    @MockBean
    private InvestmentMapper investmentMapper;

    private User user;
    private Investment investment;
    private InvestmentDTO investmentDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        investment = Investment.builder()
                .id(1L)
                .name("Test Investment")
                .investmentType("SIP")
                .initialAmount(new BigDecimal("10000.00"))
                .currentValue(new BigDecimal("11000.00"))
                .startDate(now.minusMonths(6))
                .expectedReturnRate(new BigDecimal("12.00"))
                .user(user)
                .notes("Test notes")
                .ticker("TEST")
                .build();

        investmentDTO = InvestmentDTO.builder()
                .id(1L)
                .name("Test Investment")
                .investmentType("SIP")
                .initialAmount(new BigDecimal("10000.00"))
                .currentValue(new BigDecimal("11000.00"))
                .startDate(now.minusMonths(6))
                .expectedReturnRate(new BigDecimal("12.00"))
                .userId(1L)
                .notes("Test notes")
                .ticker("TEST")
                .build();
    }

    @Test
    @WithMockUser
    void getAllInvestments_ShouldReturnAllInvestments() throws Exception {
        // Arrange
        List<Investment> investments = Arrays.asList(investment);
        when(investmentService.getAllInvestments()).thenReturn(investments);
        when(investmentMapper.toDTO(investment)).thenReturn(investmentDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/investments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Investment")))
                .andExpect(jsonPath("$[0].investmentType", is("SIP")))
                .andExpect(jsonPath("$[0].initialAmount", is(10000.00)))
                .andExpect(jsonPath("$[0].currentValue", is(11000.00)))
                .andExpect(jsonPath("$[0].expectedReturnRate", is(12.00)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].ticker", is("TEST")));

        verify(investmentService, times(1)).getAllInvestments();
        verify(investmentMapper, times(1)).toDTO(investment);
    }

    @Test
    @WithMockUser
    void getInvestmentById_WithValidId_ShouldReturnInvestment() throws Exception {
        // Arrange
        when(investmentService.getInvestmentById(1L)).thenReturn(investment);
        when(investmentMapper.toDTO(investment)).thenReturn(investmentDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/investments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Investment")))
                .andExpect(jsonPath("$.investmentType", is("SIP")))
                .andExpect(jsonPath("$.initialAmount", is(10000.00)))
                .andExpect(jsonPath("$.currentValue", is(11000.00)))
                .andExpect(jsonPath("$.expectedReturnRate", is(12.00)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.ticker", is("TEST")));

        verify(investmentService, times(1)).getInvestmentById(1L);
        verify(investmentMapper, times(1)).toDTO(investment);
    }

    @Test
    @WithMockUser
    void getInvestmentsByUserId_WithValidUserId_ShouldReturnInvestments() throws Exception {
        // Arrange
        List<Investment> investments = Arrays.asList(investment);
        when(investmentService.getInvestmentsByUserId(1L)).thenReturn(investments);
        when(investmentMapper.toDTO(investment)).thenReturn(investmentDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/investments/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Investment")))
                .andExpect(jsonPath("$[0].userId", is(1)));

        verify(investmentService, times(1)).getInvestmentsByUserId(1L);
        verify(investmentMapper, times(1)).toDTO(investment);
    }

    @Test
    @WithMockUser
    void getInvestmentsByType_WithValidType_ShouldReturnInvestments() throws Exception {
        // Arrange
        List<Investment> investments = Arrays.asList(investment);
        when(investmentService.getInvestmentsByType("SIP")).thenReturn(investments);
        when(investmentMapper.toDTO(investment)).thenReturn(investmentDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/investments/type/SIP"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Investment")))
                .andExpect(jsonPath("$[0].investmentType", is("SIP")));

        verify(investmentService, times(1)).getInvestmentsByType("SIP");
        verify(investmentMapper, times(1)).toDTO(investment);
    }

    @Test
    @WithMockUser
    void getInvestmentsByUserIdAndType_WithValidUserIdAndType_ShouldReturnInvestments() throws Exception {
        // Arrange
        List<Investment> investments = Arrays.asList(investment);
        when(investmentService.getInvestmentsByUserIdAndType(1L, "SIP")).thenReturn(investments);
        when(investmentMapper.toDTO(investment)).thenReturn(investmentDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/investments/user/1/type/SIP"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Investment")))
                .andExpect(jsonPath("$[0].investmentType", is("SIP")))
                .andExpect(jsonPath("$[0].userId", is(1)));

        verify(investmentService, times(1)).getInvestmentsByUserIdAndType(1L, "SIP");
        verify(investmentMapper, times(1)).toDTO(investment);
    }

    @Test
    @WithMockUser
    void createInvestment_WithValidInvestment_ShouldReturnCreatedInvestment() throws Exception {
        // Arrange
        when(investmentMapper.toEntity(investmentDTO)).thenReturn(investment);
        when(investmentService.createInvestment(investment, 1L)).thenReturn(investment);
        when(investmentMapper.toDTO(investment)).thenReturn(investmentDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/investments")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(investmentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Investment")))
                .andExpect(jsonPath("$.investmentType", is("SIP")))
                .andExpect(jsonPath("$.initialAmount", is(10000.00)))
                .andExpect(jsonPath("$.currentValue", is(11000.00)))
                .andExpect(jsonPath("$.expectedReturnRate", is(12.00)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.ticker", is("TEST")));

        verify(investmentMapper, times(1)).toEntity(investmentDTO);
        verify(investmentService, times(1)).createInvestment(investment, 1L);
        verify(investmentMapper, times(1)).toDTO(investment);
    }

    @Test
    @WithMockUser
    void updateInvestment_WithValidInvestment_ShouldReturnUpdatedInvestment() throws Exception {
        // Arrange
        when(investmentMapper.toEntity(investmentDTO)).thenReturn(investment);
        when(investmentService.updateInvestment(eq(1L), any(Investment.class))).thenReturn(investment);
        when(investmentMapper.toDTO(investment)).thenReturn(investmentDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/investments/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(investmentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Investment")))
                .andExpect(jsonPath("$.investmentType", is("SIP")))
                .andExpect(jsonPath("$.initialAmount", is(10000.00)))
                .andExpect(jsonPath("$.currentValue", is(11000.00)))
                .andExpect(jsonPath("$.expectedReturnRate", is(12.00)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.ticker", is("TEST")));

        verify(investmentMapper, times(1)).toEntity(investmentDTO);
        verify(investmentService, times(1)).updateInvestment(eq(1L), any(Investment.class));
        verify(investmentMapper, times(1)).toDTO(investment);
    }

    @Test
    @WithMockUser
    void deleteInvestment_WithValidId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(investmentService).deleteInvestment(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/investments/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(investmentService, times(1)).deleteInvestment(1L);
    }

    @Test
    @WithMockUser
    void updateInvestmentValue_WithValidIdAndValue_ShouldReturnUpdatedInvestment() throws Exception {
        // Arrange
        BigDecimal newValue = new BigDecimal("12000.00");
        
        Investment updatedInvestment = Investment.builder()
                .id(1L)
                .name("Test Investment")
                .investmentType("SIP")
                .initialAmount(new BigDecimal("10000.00"))
                .currentValue(newValue)
                .startDate(now.minusMonths(6))
                .expectedReturnRate(new BigDecimal("12.00"))
                .user(user)
                .notes("Test notes")
                .ticker("TEST")
                .build();
        
        InvestmentDTO updatedInvestmentDTO = InvestmentDTO.builder()
                .id(1L)
                .name("Test Investment")
                .investmentType("SIP")
                .initialAmount(new BigDecimal("10000.00"))
                .currentValue(newValue)
                .startDate(now.minusMonths(6))
                .expectedReturnRate(new BigDecimal("12.00"))
                .userId(1L)
                .notes("Test notes")
                .ticker("TEST")
                .build();
        
        when(investmentService.updateInvestmentValue(1L, newValue)).thenReturn(updatedInvestment);
        when(investmentMapper.toDTO(updatedInvestment)).thenReturn(updatedInvestmentDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/investments/1/value")
                .with(csrf())
                .param("value", "12000.00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Investment")))
                .andExpect(jsonPath("$.currentValue", is(12000.00)));

        verify(investmentService, times(1)).updateInvestmentValue(1L, newValue);
        verify(investmentMapper, times(1)).toDTO(updatedInvestment);
    }

    @Test
    @WithMockUser
    void calculateReturnRate_WithValidId_ShouldReturnReturnRate() throws Exception {
        // Arrange
        BigDecimal returnRate = new BigDecimal("20.50");
        when(investmentService.calculateReturnRate(1L)).thenReturn(returnRate);

        // Act & Assert
        mockMvc.perform(get("/api/v1/investments/1/return-rate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(20.50)));

        verify(investmentService, times(1)).calculateReturnRate(1L);
    }
}