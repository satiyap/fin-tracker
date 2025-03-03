package com.fintracker.api.v1.controller;

import com.fintracker.api.v1.dto.InvestmentDTO;
import com.fintracker.api.v1.mapper.InvestmentMapper;
import com.fintracker.core.domain.Investment;
import com.fintracker.core.service.InvestmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/investments")
@RequiredArgsConstructor
@Tag(name = "Investments", description = "Investment management API")
public class InvestmentController {

    private final InvestmentService investmentService;
    private final InvestmentMapper investmentMapper;

    @GetMapping
    @Operation(summary = "Get all investments", description = "Get a list of all investments")
    public ResponseEntity<List<InvestmentDTO>> getAllInvestments() {
        List<Investment> investments = investmentService.getAllInvestments();
        List<InvestmentDTO> investmentDTOs = investments.stream()
                .map(investmentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(investmentDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get investment by ID", description = "Get investment details by ID")
    public ResponseEntity<InvestmentDTO> getInvestmentById(@PathVariable Long id) {
        Investment investment = investmentService.getInvestmentById(id);
        return ResponseEntity.ok(investmentMapper.toDTO(investment));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get investments by user ID", description = "Get a list of investments for a specific user")
    public ResponseEntity<List<InvestmentDTO>> getInvestmentsByUserId(@PathVariable Long userId) {
        List<Investment> investments = investmentService.getInvestmentsByUserId(userId);
        List<InvestmentDTO> investmentDTOs = investments.stream()
                .map(investmentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(investmentDTOs);
    }

    @GetMapping("/type/{investmentType}")
    @Operation(summary = "Get investments by type", description = "Get a list of investments by type")
    public ResponseEntity<List<InvestmentDTO>> getInvestmentsByType(@PathVariable String investmentType) {
        List<Investment> investments = investmentService.getInvestmentsByType(investmentType);
        List<InvestmentDTO> investmentDTOs = investments.stream()
                .map(investmentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(investmentDTOs);
    }

    @GetMapping("/user/{userId}/type/{investmentType}")
    @Operation(summary = "Get investments by user ID and type", description = "Get a list of investments for a specific user and type")
    public ResponseEntity<List<InvestmentDTO>> getInvestmentsByUserIdAndType(
            @PathVariable Long userId, @PathVariable String investmentType) {
        List<Investment> investments = investmentService.getInvestmentsByUserIdAndType(userId, investmentType);
        List<InvestmentDTO> investmentDTOs = investments.stream()
                .map(investmentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(investmentDTOs);
    }

    @PostMapping
    @Operation(summary = "Create investment", description = "Create a new investment")
    public ResponseEntity<InvestmentDTO> createInvestment(@Valid @RequestBody InvestmentDTO investmentDTO) {
        Investment investment = investmentMapper.toEntity(investmentDTO);
        Investment savedInvestment = investmentService.createInvestment(investment, investmentDTO.getUserId());
        return ResponseEntity.ok(investmentMapper.toDTO(savedInvestment));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update investment", description = "Update an existing investment")
    public ResponseEntity<InvestmentDTO> updateInvestment(@PathVariable Long id, @Valid @RequestBody InvestmentDTO investmentDTO) {
        Investment investment = investmentMapper.toEntity(investmentDTO);
        Investment updatedInvestment = investmentService.updateInvestment(id, investment);
        return ResponseEntity.ok(investmentMapper.toDTO(updatedInvestment));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete investment", description = "Delete an investment by ID")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/value")
    @Operation(summary = "Update investment value", description = "Update the current value of an investment")
    public ResponseEntity<InvestmentDTO> updateInvestmentValue(@PathVariable Long id, @RequestParam BigDecimal value) {
        Investment updatedInvestment = investmentService.updateInvestmentValue(id, value);
        return ResponseEntity.ok(investmentMapper.toDTO(updatedInvestment));
    }

    @GetMapping("/{id}/return-rate")
    @Operation(summary = "Calculate return rate", description = "Calculate the return rate of an investment")
    public ResponseEntity<BigDecimal> calculateReturnRate(@PathVariable Long id) {
        BigDecimal returnRate = investmentService.calculateReturnRate(id);
        return ResponseEntity.ok(returnRate);
    }
}