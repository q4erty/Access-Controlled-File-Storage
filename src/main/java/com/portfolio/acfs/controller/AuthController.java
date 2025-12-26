package com.portfolio.acfs.controller;

import com.portfolio.acfs.dto.*;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request.getEmail(), request.getPassword());

        AuthResponse response = AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.authenticate(request.getEmail(), request.getPassword());

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<Void> request(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset/confirm")
    public ResponseEntity<Void> confirm(@RequestBody ResetPasswordRequest dto) {
        authService.confirmPasswordReset(dto);
        return ResponseEntity.ok().build();
    }

}