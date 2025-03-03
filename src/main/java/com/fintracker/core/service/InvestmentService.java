package com.fintracker.core.service;

import com.fintracker.core.domain.Investment;
import com.fintracker.core.domain.User;
import com.fintracker.core.exception.ResourceNotFoundException;
import com.fintracker.core.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Investment> getAllInvestments() {
        return investmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Investment getInvestmentById(Long id) {
        return investmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Investment> getInvestmentsByUserId(Long userId) {
        return investmentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Investment> getInvestmentsByType(String investmentType) {
        return investmentRepository.findByInvestmentType(investmentType);
    }

    @Transactional(readOnly = true)
    public List<Investment> getInvestmentsByUserIdAndType(Long userId, String investmentType) {
        return investmentRepository.findByUserIdAndInvestmentType(userId, investmentType);
    }

    @Transactional
    public Investment createInvestment(Investment investment, Long userId) {
        User user = userService.getUserById(userId);
        investment.setUser(user);
        
        // Set initial current value to initial amount
        if (investment.getCurrentValue() == null) {
            investment.setCurrentValue(investment.getInitialAmount());
        }
        
        return investmentRepository.save(investment);
    }

    @Transactional
    public Investment updateInvestment(Long id, Investment investmentDetails) {
        Investment investment = getInvestmentById(id);
        
        investment.setName(investmentDetails.getName());
        investment.setInvestmentType(investmentDetails.getInvestmentType());
        investment.setInitialAmount(investmentDetails.getInitialAmount());
        investment.setCurrentValue(investmentDetails.getCurrentValue());
        investment.setStartDate(investmentDetails.getStartDate());
        investment.setEndDate(investmentDetails.getEndDate());
        investment.setExpectedReturnRate(investmentDetails.getExpectedReturnRate());
        investment.setNotes(investmentDetails.getNotes());
        investment.setTicker(investmentDetails.getTicker());
        
        return investmentRepository.save(investment);
    }

    @Transactional
    public void deleteInvestment(Long id) {
        Investment investment = getInvestmentById(id);
        investmentRepository.delete(investment);
    }

    @Transactional
    public Investment updateInvestmentValue(Long id, BigDecimal newValue) {
        Investment investment = getInvestmentById(id);
        investment.setCurrentValue(newValue);
        return investmentRepository.save(investment);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateReturnRate(Long id) {
        Investment investment = getInvestmentById(id);
        
        if (investment.getCurrentValue() == null || investment.getInitialAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        // Calculate total return
        BigDecimal totalReturn = investment.getCurrentValue().subtract(investment.getInitialAmount());
        
        // Calculate return percentage
        BigDecimal returnPercentage = totalReturn.divide(investment.getInitialAmount(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
        
        // Calculate time period in years
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(investment.getStartDate(), now);
        BigDecimal yearsBetween = new BigDecimal(daysBetween).divide(new BigDecimal("365"), 4, BigDecimal.ROUND_HALF_UP);
        
        // Calculate annualized return rate if investment has been held for more than a month
        if (yearsBetween.compareTo(new BigDecimal("0.0833")) > 0) { // More than 1/12 of a year (1 month)
            // Using the formula: (1 + r)^t = FV/PV, where r is the annualized return rate
            // Solving for r: r = (FV/PV)^(1/t) - 1
            
            BigDecimal fvPvRatio = investment.getCurrentValue().divide(investment.getInitialAmount(), 8, BigDecimal.ROUND_HALF_UP);
            
            // This is a simplified calculation and might not be accurate for all investment types
            BigDecimal annualizedReturnRate = BigDecimal.valueOf(Math.pow(fvPvRatio.doubleValue(), 1.0 / yearsBetween.doubleValue()) - 1)
                    .multiply(new BigDecimal("100"));
            
            return annualizedReturnRate.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        
        // For short-term investments, just return the simple return percentage
        return returnPercentage.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}