package com.portfolio.acfs.repository;

import com.portfolio.acfs.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByOwnerId(Long ownerId);
    List<FileEntity> findByFolderId(Long folderId);
    List<FileEntity> findByOwnerIdAndFolderIsNull(Long ownerId);
}