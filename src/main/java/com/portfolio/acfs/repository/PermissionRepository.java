package com.portfolio.acfs.repository;

import com.portfolio.acfs.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByFileIdAndUserId(Long fileId, Long userId);
    Optional<Permission> findByFileIdAndUserIdAndType(Long fileId, Long userId, Permission.PermissionType type);
    List<Permission> findByFileId(Long fileId);
    void deleteByFileId(Long fileId);
}