package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.entity.FileEntity;
import com.portfolio.acfs.entity.Permission;
import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.repository.FileRepository;
import com.portfolio.acfs.repository.PermissionRepository;
import com.portfolio.acfs.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PermissionServiceImplTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private User owner;
    private User otherUser;
    private FileEntity file;
    private Permission permission;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        owner = User.builder()
                .id(1L)
                .email("owner@example.com")
                .roles(Set.of(Role.builder().name(Role.RoleType.USER).build()))
                .build();

        otherUser = User.builder()
                .id(2L)
                .email("user@example.com")
                .roles(Set.of(Role.builder().name(Role.RoleType.USER).build()))
                .build();

        file = FileEntity.builder()
                .id(1L)
                .filename("file.txt")
                .owner(owner)
                .build();

        permission = Permission.builder()
                .id(1L)
                .file(file)
                .user(otherUser)
                .type(Permission.PermissionType.READ)
                .build();
    }

    @Test
    void grantPermissionShouldSavePermission() {
        when(userService.getCurrentUser()).thenReturn(owner);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(permissionRepository.save(any(Permission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Permission saved = permissionService.grantPermission(1L, 2L, Permission.PermissionType.READ);

        assertNotNull(saved);
        assertEquals(otherUser, saved.getUser());
        assertEquals(file, saved.getFile());
        assertEquals(Permission.PermissionType.READ, saved.getType());
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void revokePermissionShouldDeletePermission() {
        when(userService.getCurrentUser()).thenReturn(owner);
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        permissionService.revokePermission(1L);

        verify(permissionRepository).delete(permission);
    }

    @Test
    void getFilePermissionsShouldReturnList() {
        when(userService.getCurrentUser()).thenReturn(owner);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(permissionRepository.findByFileId(1L)).thenReturn(List.of(permission));

        List<Permission> perms = permissionService.getFilePermissions(1L);

        assertEquals(1, perms.size());
        assertEquals(permission, perms.get(0));
    }

    @Test
    void getUserPermissionsShouldReturnListIfOwner() {
        when(userService.getCurrentUser()).thenReturn(owner);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(permissionRepository.findByFileIdAndUserId(1L, 2L)).thenReturn(List.of(permission));

        List<Permission> perms = permissionService.getUserPermissions(1L, 2L);

        assertEquals(1, perms.size());
        assertEquals(permission, perms.get(0));
    }

    @Test
    void getUserPermissionsShouldReturnListIfCurrentUser() {
        when(userService.getCurrentUser()).thenReturn(otherUser);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(permissionRepository.findByFileIdAndUserId(1L, 2L)).thenReturn(List.of(permission));

        List<Permission> perms = permissionService.getUserPermissions(1L, 2L);

        assertEquals(1, perms.size());
        assertEquals(permission, perms.get(0));
    }

    @Test
    void grantPermissionShouldThrowIfNotOwnerOrAdmin() {
        User unauthorized = User.builder().id(3L).build();
        when(userService.getCurrentUser()).thenReturn(unauthorized);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> permissionService.grantPermission(1L, 2L, Permission.PermissionType.READ));

        assertEquals("Only owner or admin can grant permissions", ex.getMessage());
    }
}
