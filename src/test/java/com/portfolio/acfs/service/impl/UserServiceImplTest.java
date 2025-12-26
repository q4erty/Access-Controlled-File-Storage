package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.repository.RoleRepository;
import com.portfolio.acfs.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User admin;
    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        admin = User.builder()
                .id(1L)
                .email("admin@example.com")
                .roles(new HashSet<>(Set.of(Role.builder().name(Role.RoleType.ADMIN).build())))
                .build();

        user = User.builder()
                .id(2L)
                .email("user@example.com")
                .roles(new HashSet<>(Set.of(Role.builder().name(Role.RoleType.USER).build())))
                .build();
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void getCurrentUserShouldReturnUser() {
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User current = userService.getCurrentUser();

        assertEquals(user, current);
    }

    @Test
    void getAllUsersShouldReturnListForAdmin() {
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findAll()).thenReturn(List.of(admin, user));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void getAllUsersShouldThrowForNonAdmin() {
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getAllUsers());
        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void addRoleToUserShouldAssignRoleForAdmin() {
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleType.ADMIN)).thenReturn(Optional.of(Role.builder().name(Role.RoleType.ADMIN).build()));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.addRoleToUser(2L, Role.RoleType.ADMIN);

        assertTrue(updated.getRoles().stream().anyMatch(r -> r.getName() == Role.RoleType.ADMIN));
    }

    @Test
    void addRoleToUserShouldThrowIfNotAdmin() {
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.addRoleToUser(2L, Role.RoleType.ADMIN));
        assertEquals("Only admin can assign roles", ex.getMessage());
    }

    @Test
    void removeRoleFromUserShouldRemoveRole() {
        user.getRoles().add(Role.builder().name(Role.RoleType.ADMIN).build());

        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.removeRoleFromUser(2L, Role.RoleType.ADMIN);

        assertTrue(updated.getRoles().stream().noneMatch(r -> r.getName() == Role.RoleType.ADMIN));
    }

    @Test
    void disableUserShouldSetEnabledFalse() {
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.disableUser(2L);

        assertFalse(user.getEnabled());
    }

    @Test
    void enableUserShouldSetEnabledTrue() {
        user.setEnabled(false);

        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.enableUser(2L);

        assertTrue(user.getEnabled());
    }

    @Test
    void getUserByIdShouldThrowIfNotAdminAndNotSelf() {
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void getUserByIdShouldReturnUserIfAdminOrSelf() {
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        User found = userService.getUserById(2L);

        assertEquals(user, found);
    }
}
