package com.fintracker.api.v1.mapper;

import com.fintracker.api.v1.dto.InvestmentDTO;
import com.fintracker.core.domain.Investment;
import com.fintracker.core.domain.User;
import org.springframework.stereotype.Component;

@Component
public class InvestmentMapper {
    
    public InvestmentDTO toDTO(Investment investment) {
        if (investment == null) {
            return null;
        }
        
        return InvestmentDTO.builder()
                .id(investment.getId())
                .name(investment.getName())
                .investmentType(investment.getInvestmentType())
                .initialAmount(investment.getInitialAmount())
                .currentValue(investment.getCurrentValue())
                .startDate(investment.getStartDate())
                .endDate(investment.getEndDate())
                .expectedReturnRate(investment.getExpectedReturnRate())
                .userId(investment.getUser() != null ? investment.getUser().getId() : null)
                .notes(investment.getNotes())
                .ticker(investment.getTicker())
                .build();
    }
    
    public Investment toEntity(InvestmentDTO investmentDTO) {
        if (investmentDTO == null) {
            return null;
        }
        
        Investment investment = Investment.builder()
                .id(investmentDTO.getId())
                .name(investmentDTO.getName())
                .investmentType(investmentDTO.getInvestmentType())
                .initialAmount(investmentDTO.getInitialAmount())
                .currentValue(investmentDTO.getCurrentValue())
                .startDate(investmentDTO.getStartDate())
                .endDate(investmentDTO.getEndDate())
                .expectedReturnRate(investmentDTO.getExpectedReturnRate())
                .notes(investmentDTO.getNotes())
                .ticker(investmentDTO.getTicker())
                .build();
        
        if (investmentDTO.getUserId() != null) {
            User user = new User();
            user.setId(investmentDTO.getUserId());
            investment.setUser(user);
        }
        
        return investment;
    }
}