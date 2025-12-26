package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.entity.*;
import com.portfolio.acfs.repository.FileRepository;
import com.portfolio.acfs.repository.FolderRepository;
import com.portfolio.acfs.repository.PermissionRepository;
import com.portfolio.acfs.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final PermissionRepository permissionRepository;
    private final UserServiceImpl userService;

    @Value("${file.storage.location:uploads}")
    protected String storageLocation;

    @Transactional
    public FileEntity uploadFile(MultipartFile file, Long folderId) throws IOException {
        User owner = userService.getCurrentUser();

        Folder folder = null;
        if (folderId != null) {
            folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));

            if (!canAccessFolder(owner, folder)) {
                throw new RuntimeException("Access denied to folder");
            }
        }

        Path uploadPath = Paths.get(storageLocation);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = file.getOriginalFilename();
        String storagePath = UUID.randomUUID().toString() + "_" + filename;
        Path filePath = uploadPath.resolve(storagePath);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        FileEntity fileEntity = FileEntity.builder()
                .filename(filename)
                .contentType(file.getContentType())
                .size(file.getSize())
                .storagePath(storagePath)
                .owner(owner)
                .folder(folder)
                .build();

        return fileRepository.save(fileEntity);
    }

    public FileEntity getFile(Long fileId) {
        User user = userService.getCurrentUser();

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!hasAccess(user, file, Permission.PermissionType.READ)) {
            throw new RuntimeException("Access denied");
        }

        return file;
    }

    public List<FileEntity> getFilesInFolder(Long folderId) {
        User user = userService.getCurrentUser();

        if (folderId != null) {
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));

            if (!canAccessFolder(user, folder)) {
                throw new RuntimeException("Access denied");
            }

            return fileRepository.findByFolderId(folderId);
        } else {
            return fileRepository.findByOwnerIdAndFolderIsNull(user.getId());
        }
    }

    public List<FileEntity> getUserFiles() {
        User user = userService.getCurrentUser();
        return fileRepository.findByOwnerId(user.getId());
    }

    public byte[] downloadFile(Long fileId) throws IOException {
        FileEntity file = getFile(fileId);
        Path filePath = Paths.get(storageLocation).resolve(file.getStoragePath());
        return Files.readAllBytes(filePath);
    }

    @Transactional
    public FileEntity moveFile(Long fileId, Long targetFolderId) {
        User user = userService.getCurrentUser();

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!hasAccess(user, file, Permission.PermissionType.WRITE)) {
            throw new RuntimeException("Access denied");
        }

        Folder targetFolder = null;
        if (targetFolderId != null) {
            targetFolder = folderRepository.findById(targetFolderId)
                    .orElseThrow(() -> new RuntimeException("Target folder not found"));

            if (!canAccessFolder(user, targetFolder)) {
                throw new RuntimeException("Access denied to target folder");
            }
        }

        file.setFolder(targetFolder);
        return fileRepository.save(file);
    }

    @Transactional
    public FileEntity renameFile(Long fileId, String newFilename) {
        User user = userService.getCurrentUser();

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!hasAccess(user, file, Permission.PermissionType.WRITE)) {
            throw new RuntimeException("Access denied");
        }

        file.setFilename(newFilename);
        return fileRepository.save(file);
    }

    @Transactional
    public void deleteFile(Long fileId) throws IOException {
        User user = userService.getCurrentUser();

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!hasAccess(user, file, Permission.PermissionType.DELETE)) {
            throw new RuntimeException("Access denied");
        }

        Path filePath = Paths.get(storageLocation).resolve(file.getStoragePath());
        Files.deleteIfExists(filePath);

        permissionRepository.deleteByFileId(fileId);
        fileRepository.delete(file);
    }

    private boolean hasAccess(User user, FileEntity file, Permission.PermissionType permissionType) {
        if (file.getOwner().getId().equals(user.getId())) {
            return true;
        }

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ADMIN);
        if (isAdmin) {
            return true;
        }

        return permissionRepository.findByFileIdAndUserIdAndType(file.getId(), user.getId(), permissionType)
                .isPresent();
    }

    private boolean canAccessFolder(User user, Folder folder) {
        if (folder.getOwner().getId().equals(user.getId())) {
            return true;
        }
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleType.ADMIN);
    }
}