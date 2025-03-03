package com.fintracker.api.v1.controller;

import com.fintracker.api.v1.dto.AuthRequest;
import com.fintracker.api.v1.dto.AuthResponse;
import com.fintracker.api.v1.dto.UserDTO;
import com.fintracker.api.v1.mapper.UserMapper;
import com.fintracker.core.domain.User;
import com.fintracker.core.service.UserService;
import com.fintracker.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);
        
        User user = userService.getUserByUsername(authRequest.getUsername());
        
        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .build());
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register a new user")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(userMapper.toDTO(savedUser));
    }
}