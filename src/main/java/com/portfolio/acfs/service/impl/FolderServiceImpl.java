package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.entity.Folder;
import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.repository.FolderRepository;
import com.portfolio.acfs.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final UserServiceImpl userService;

    @Transactional
    public Folder createFolder(String name, Long parentId) {
        User currentUser = userService.getCurrentUser();

        Folder parent = null;
        if (parentId != null) {
            parent = folderRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));

            if (!canAccessFolder(currentUser, parent)) {
                throw new RuntimeException("Access denied to parent folder");
            }
        }

        Folder folder = Folder.builder()
                .name(name)
                .owner(currentUser)
                .parent(parent)
                .build();

        return folderRepository.save(folder);
    }

    public Folder getFolderById(Long folderId) {
        User currentUser = userService.getCurrentUser();

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (!canAccessFolder(currentUser, folder)) {
            throw new RuntimeException("Access denied");
        }

        return folder;
    }

    public List<Folder> getRootFolders() {
        User currentUser = userService.getCurrentUser();

        if (hasRole(currentUser, Role.RoleType.ADMIN)) {
            return folderRepository.findAll().stream()
                    .filter(f -> f.getParent() == null)
                    .toList();
        }

        return folderRepository.findByOwnerIdAndParentIsNull(currentUser.getId());
    }

    public List<Folder> getSubfolders(Long parentId) {
        User currentUser = userService.getCurrentUser();

        Folder parent = folderRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent folder not found"));

        if (!canAccessFolder(currentUser, parent)) {
            throw new RuntimeException("Access denied");
        }

        return folderRepository.findByParentId(parentId);
    }

    public List<Folder> getUserFolders() {
        User currentUser = userService.getCurrentUser();
        return folderRepository.findByOwnerId(currentUser.getId());
    }

    @Transactional
    public Folder updateFolder(Long folderId, String name) {
        User currentUser = userService.getCurrentUser();

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (!canModifyFolder(currentUser, folder)) {
            throw new RuntimeException("Access denied");
        }

        folder.setName(name);
        return folderRepository.save(folder);
    }

    @Transactional
    public Folder moveFolder(Long folderId, Long newParentId) {
        User currentUser = userService.getCurrentUser();

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (!canModifyFolder(currentUser, folder)) {
            throw new RuntimeException("Access denied");
        }

        if (newParentId != null) {
            Folder newParent = folderRepository.findById(newParentId)
                    .orElseThrow(() -> new RuntimeException("New parent folder not found"));

            if (!canAccessFolder(currentUser, newParent)) {
                throw new RuntimeException("Access denied to new parent folder");
            }

            if (isDescendant(newParent, folder)) {
                throw new RuntimeException("Cannot move folder to its own descendant");
            }

            folder.setParent(newParent);
        } else {
            folder.setParent(null);
        }

        return folderRepository.save(folder);
    }

    @Transactional
    public void deleteFolder(Long folderId) {
        User currentUser = userService.getCurrentUser();

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (!canModifyFolder(currentUser, folder)) {
            throw new RuntimeException("Access denied");
        }

        folderRepository.delete(folder);
    }

    private boolean canAccessFolder(User user, Folder folder) {
        if (folder.getOwner().getId().equals(user.getId())) {
            return true;
        }
        return hasRole(user, Role.RoleType.ADMIN);
    }

    private boolean canModifyFolder(User user, Folder folder) {
        if (folder.getOwner().getId().equals(user.getId())) {
            return true;
        }
        return hasRole(user, Role.RoleType.ADMIN);
    }

    private boolean isDescendant(Folder potentialDescendant, Folder ancestor) {
        Folder current = potentialDescendant;
        while (current != null) {
            if (current.getId().equals(ancestor.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private boolean hasRole(User user, Role.RoleType roleType) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == roleType);
    }
}