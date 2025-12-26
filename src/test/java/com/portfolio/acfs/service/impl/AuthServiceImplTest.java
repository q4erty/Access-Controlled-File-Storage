package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.repository.RoleRepository;
import com.portfolio.acfs.repository.UserRepository;
import com.portfolio.acfs.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    private final String email = "test@example.com";
    private final String password = "password123";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerShouldCreateUser() {
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByName(Role.RoleType.USER))
                .thenReturn(Optional.of(new Role(1L, Role.RoleType.USER)));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = authService.register(email, password);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertTrue(user.getEnabled());
        assertEquals(1, user.getRoles().size());
        assertEquals("encodedPassword", user.getPassword());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerShouldThrowIfEmailExists() {
        when(userRepository.existsByEmail(email)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(email, password));
        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticateShouldReturnToken() {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(mock(org.springframework.security.core.userdetails.User.class));
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");

        String token = authService.authenticate(email, password);

        assertNotNull(token);
        assertEquals("jwt-token", token);

        verify(authenticationManager).authenticate(authToken);
        verify(jwtTokenProvider).generateToken(any());
    }
}
