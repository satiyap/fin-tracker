package com.fintracker.api.v1.mapper;

import com.fintracker.api.v1.dto.UserDTO;
import com.fintracker.core.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
    
    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        
        return User.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .build();
    }
}