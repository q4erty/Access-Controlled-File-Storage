package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.entity.FileEntity;
import com.portfolio.acfs.entity.Permission;
import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.repository.FileRepository;
import com.portfolio.acfs.repository.PermissionRepository;
import com.portfolio.acfs.repository.UserRepository;
import com.portfolio.acfs.service.PermissionService;
import com.portfolio.acfs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public Permission grantPermission(Long fileId, Long userId, Permission.PermissionType permissionType) {
        User currentUser = userService.getCurrentUser();

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!canManagePermissions(currentUser, file)) {
            throw new RuntimeException("Only owner or admin can grant permissions");
        }

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        Permission permission = Permission.builder()
                .user(targetUser)
                .file(file)
                .type(permissionType)
                .build();

        return permissionRepository.save(permission);
    }

    @Transactional
    public void revokePermission(Long permissionId) {
        User currentUser = userService.getCurrentUser();

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        if (!canManagePermissions(currentUser, permission.getFile())) {
            throw new RuntimeException("Only owner or admin can revoke permissions");
        }

        permissionRepository.delete(permission);
    }

    public List<Permission> getFilePermissions(Long fileId) {
        User currentUser = userService.getCurrentUser();

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!canManagePermissions(currentUser, file)) {
            throw new RuntimeException("Access denied");
        }

        return permissionRepository.findByFileId(fileId);
    }

    public List<Permission> getUserPermissions(Long fileId, Long userId) {
        User currentUser = userService.getCurrentUser();

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!canManagePermissions(currentUser, file) && !currentUser.getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return permissionRepository.findByFileIdAndUserId(fileId, userId);
    }

    private boolean canManagePermissions(User user, FileEntity file) {
        if (file.getOwner().getId().equals(user.getId())) {
            return true;
        }
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ADMIN);
    }
}
