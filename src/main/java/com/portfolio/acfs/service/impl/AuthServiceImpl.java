package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.dto.ResetPasswordRequest;
import com.portfolio.acfs.entity.PasswordResetToken;
import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.repository.PasswordResetTokenRepository;
import com.portfolio.acfs.repository.RoleRepository;
import com.portfolio.acfs.repository.UserRepository;
import com.portfolio.acfs.service.AuthService;
import com.portfolio.acfs.service.MailService;
import com.portfolio.acfs.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final MailService mailService;

    @Transactional
    public User register(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        Role userRole = roleRepository.findByName(Role.RoleType.USER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(Role.RoleType.USER)
                        .build()));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .roles(roles)
                .build();

        return userRepository.save(user);
    }

    public String authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        var userDetails = userDetailsService.loadUserByUsername(email);
        return jwtTokenProvider.generateToken(userDetails);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        passwordResetTokenRepository.deleteByEmail(email);

        String code = String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 999999)
        );

        PasswordResetToken token = PasswordResetToken.builder()
                .email(email)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        passwordResetTokenRepository.save(token);

        mailService.sendPasswordResetCode(email, code);
    }

    @Transactional
    public void confirmPasswordReset(ResetPasswordRequest request) {

        PasswordResetToken token =
                passwordResetTokenRepository
                        .findByEmailAndCode(
                                request.getEmail(),
                                request.getCode()
                        )
                        .orElseThrow(() -> new RuntimeException("Invalid code"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Code expired");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.deleteByEmail(request.getEmail());
    }


}