package com.fintracker.core.repository;

import com.fintracker.core.domain.Investment;
import com.fintracker.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByUser(User user);
    List<Investment> findByUserId(Long userId);
    List<Investment> findByInvestmentType(String investmentType);
    List<Investment> findByUserIdAndInvestmentType(Long userId, String investmentType);
}