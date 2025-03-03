package com.fintracker.core.service;

import com.fintracker.core.domain.Investment;
import com.fintracker.core.domain.User;
import com.fintracker.core.exception.ResourceNotFoundException;
import com.fintracker.core.repository.InvestmentRepository;
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
public class InvestmentServiceTest {

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private InvestmentService investmentService;

    private User user;
    private Investment investment;
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
    }

    @Test
    void getAllInvestments_ShouldReturnAllInvestments() {
        // Arrange
        List<Investment> investments = Arrays.asList(investment);
        when(investmentRepository.findAll()).thenReturn(investments);

        // Act
        List<Investment> result = investmentService.getAllInvestments();

        // Assert
        assertEquals(1, result.size());
        verify(investmentRepository, times(1)).findAll();
    }

    @Test
    void getInvestmentById_WithValidId_ShouldReturnInvestment() {
        // Arrange
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        // Act
        Investment result = investmentService.getInvestmentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Investment", result.getName());
        verify(investmentRepository, times(1)).findById(1L);
    }

    @Test
    void getInvestmentById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(investmentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> investmentService.getInvestmentById(999L));
        verify(investmentRepository, times(1)).findById(999L);
    }

    @Test
    void getInvestmentsByUserId_ShouldReturnInvestmentsForUser() {
        // Arrange
        List<Investment> investments = Arrays.asList(investment);
        when(investmentRepository.findByUserId(1L)).thenReturn(investments);

        // Act
        List<Investment> result = investmentService.getInvestmentsByUserId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Investment", result.get(0).getName());
        verify(investmentRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getInvestmentsByType_ShouldReturnInvestmentsOfSpecifiedType() {
        // Arrange
        List<Investment> investments = Arrays.asList(investment);
        when(investmentRepository.findByInvestmentType("SIP")).thenReturn(investments);

        // Act
        List<Investment> result = investmentService.getInvestmentsByType("SIP");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Investment", result.get(0).getName());
        verify(investmentRepository, times(1)).findByInvestmentType("SIP");
    }

    @Test
    void getInvestmentsByUserIdAndType_ShouldReturnInvestmentsForUserAndType() {
        // Arrange
        List<Investment> investments = Arrays.asList(investment);
        when(investmentRepository.findByUserIdAndInvestmentType(1L, "SIP")).thenReturn(investments);

        // Act
        List<Investment> result = investmentService.getInvestmentsByUserIdAndType(1L, "SIP");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Investment", result.get(0).getName());
        verify(investmentRepository, times(1)).findByUserIdAndInvestmentType(1L, "SIP");
    }

    @Test
    void createInvestment_WithValidInvestment_ShouldReturnSavedInvestment() {
        // Arrange
        Investment newInvestment = Investment.builder()
                .name("New Investment")
                .investmentType("MUTUAL_FUND")
                .initialAmount(new BigDecimal("5000.00"))
                .startDate(now)
                .expectedReturnRate(new BigDecimal("10.00"))
                .notes("New investment notes")
                .ticker("NEW")
                .build();

        when(userService.getUserById(1L)).thenReturn(user);
        when(investmentRepository.save(any(Investment.class))).thenReturn(newInvestment);

        // Act
        Investment result = investmentService.createInvestment(newInvestment, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("New Investment", result.getName());
        verify(userService, times(1)).getUserById(1L);
        verify(investmentRepository, times(1)).save(any(Investment.class));
    }

    @Test
    void updateInvestment_WithValidInvestment_ShouldReturnUpdatedInvestment() {
        // Arrange
        Investment updatedInvestment = Investment.builder()
                .id(1L)
                .name("Updated Investment")
                .investmentType("SIP")
                .initialAmount(new BigDecimal("10000.00"))
                .currentValue(new BigDecimal("12000.00"))
                .startDate(now.minusMonths(6))
                .expectedReturnRate(new BigDecimal("15.00"))
                .user(user)
                .notes("Updated notes")
                .ticker("TEST")
                .build();

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any(Investment.class))).thenReturn(updatedInvestment);

        // Act
        Investment result = investmentService.updateInvestment(1L, updatedInvestment);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Investment", result.getName());
        assertEquals(new BigDecimal("12000.00"), result.getCurrentValue());
        assertEquals(new BigDecimal("15.00"), result.getExpectedReturnRate());
        verify(investmentRepository, times(1)).findById(1L);
        verify(investmentRepository, times(1)).save(any(Investment.class));
    }

    @Test
    void deleteInvestment_WithValidId_ShouldDeleteInvestment() {
        // Arrange
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));
        doNothing().when(investmentRepository).delete(investment);

        // Act
        investmentService.deleteInvestment(1L);

        // Assert
        verify(investmentRepository, times(1)).findById(1L);
        verify(investmentRepository, times(1)).delete(investment);
    }

    @Test
    void updateInvestmentValue_WithValidIdAndValue_ShouldUpdateAndReturnInvestment() {
        // Arrange
        BigDecimal newValue = new BigDecimal("12500.00");
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));
        
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
        
        when(investmentRepository.save(any(Investment.class))).thenReturn(updatedInvestment);

        // Act
        Investment result = investmentService.updateInvestmentValue(1L, newValue);

        // Assert
        assertNotNull(result);
        assertEquals(newValue, result.getCurrentValue());
        verify(investmentRepository, times(1)).findById(1L);
        verify(investmentRepository, times(1)).save(any(Investment.class));
    }

    @Test
    void calculateReturnRate_WithValidInvestment_ShouldReturnCorrectRate() {
        // Arrange
        when(investmentRepository.findById(1L)).thenReturn(Optional.of(investment));

        // Act
        BigDecimal result = investmentService.calculateReturnRate(1L);

        // Assert
        assertNotNull(result);
        verify(investmentRepository, times(1)).findById(1L);
    }
}