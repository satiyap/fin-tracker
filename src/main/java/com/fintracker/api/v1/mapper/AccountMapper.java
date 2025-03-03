package com.fintracker.api.v1.mapper;

import com.fintracker.api.v1.dto.AccountDTO;
import com.fintracker.core.domain.Account;
import com.fintracker.core.domain.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    
    public AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }
        
        return AccountDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .userId(account.getUser() != null ? account.getUser().getId() : null)
                .build();
    }
    
    public Account toEntity(AccountDTO accountDTO) {
        if (accountDTO == null) {
            return null;
        }
        
        Account account = Account.builder()
                .id(accountDTO.getId())
                .name(accountDTO.getName())
                .accountType(accountDTO.getAccountType())
                .balance(accountDTO.getBalance())
                .build();
        
        if (accountDTO.getUserId() != null) {
            User user = new User();
            user.setId(accountDTO.getUserId());
            account.setUser(user);
        }
        
        return account;
    }
}