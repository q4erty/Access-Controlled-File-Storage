package com.portfolio.acfs.service;

import com.portfolio.acfs.entity.Permission;
import com.portfolio.acfs.entity.Permission.PermissionType;
import java.util.List;

public interface PermissionService {

    Permission grantPermission(Long fileId, Long userId, PermissionType permissionType);

    void revokePermission(Long permissionId);

    List<Permission> getFilePermissions(Long fileId);

    List<Permission> getUserPermissions(Long fileId, Long userId);
}
