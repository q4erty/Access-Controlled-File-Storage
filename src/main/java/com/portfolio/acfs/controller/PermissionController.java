package com.portfolio.acfs.controller;

import com.portfolio.acfs.dto.*;
import com.portfolio.acfs.entity.Permission;
import com.portfolio.acfs.mapper.PermissionMapper;
import com.portfolio.acfs.service.impl.PermissionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionServiceImpl permissionService;
    private final PermissionMapper permissionMapper;

    @PostMapping
    public ResponseEntity<PermissionResponse> grantPermission(@Valid @RequestBody GrantPermissionRequest request) {
        Permission.PermissionType type = Permission.PermissionType.valueOf(request.getType());
        Permission permission = permissionService.grantPermission(request.getFileId(), request.getUserId(), type);
        return ResponseEntity.ok(permissionMapper.toPermissionResponse(permission));
    }

    @DeleteMapping("/{permissionId}")
    public ResponseEntity<MessageResponse> revokePermission(@PathVariable Long permissionId) {
        permissionService.revokePermission(permissionId);
        return ResponseEntity.ok(new MessageResponse("Permission revoked successfully"));
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<List<PermissionUserResponse>> getFilePermissions(@PathVariable Long fileId) {
        List<Permission> permissions = permissionService.getFilePermissions(fileId);
        return ResponseEntity.ok(permissionMapper.toPermissionUserResponseList(permissions));
    }

    @GetMapping("/file/{fileId}/user/{userId}")
    public ResponseEntity<List<PermissionDetailResponse>> getUserFilePermissions(
            @PathVariable Long fileId,
            @PathVariable Long userId) {
        List<Permission> permissions = permissionService.getUserPermissions(fileId, userId);
        return ResponseEntity.ok(permissionMapper.toPermissionDetailResponseList(permissions));
    }
}