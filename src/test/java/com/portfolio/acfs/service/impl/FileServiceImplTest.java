package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.entity.FileEntity;
import com.portfolio.acfs.entity.Folder;
import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.repository.FileRepository;
import com.portfolio.acfs.repository.FolderRepository;
import com.portfolio.acfs.repository.PermissionRepository;
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

class FileServiceImplTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private FileServiceImpl fileService;

    private User user;
    private FileEntity file;
    private Folder folder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        fileService.storageLocation = "target/test-uploads";

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .roles(Set.of(Role.builder().name(Role.RoleType.USER).build()))
                .build();

        folder = Folder.builder().id(1L).name("Folder").owner(user).build();

        file = FileEntity.builder()
                .id(1L)
                .filename("file.txt")
                .owner(user)
                .folder(folder)
                .build();
    }

    @Test
    void getFileShouldReturnFileIfOwner() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        FileEntity found = fileService.getFile(1L);

        assertNotNull(found);
        assertEquals(file.getId(), found.getId());
    }

    @Test
    void getFileShouldThrowIfNotOwnerAndNoPermission() {
        User otherUser = User.builder().id(2L).build();
        when(userService.getCurrentUser()).thenReturn(otherUser);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(permissionRepository.findByFileIdAndUserIdAndType(anyLong(), anyLong(), any())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> fileService.getFile(1L));
        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void getFilesInFolderShouldReturnFiles() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));
        when(fileRepository.findByFolderId(1L)).thenReturn(List.of(file));

        List<FileEntity> files = fileService.getFilesInFolder(1L);

        assertFalse(files.isEmpty());
        assertEquals(1, files.size());
        assertEquals(file, files.get(0));
    }

    @Test
    void moveFileShouldChangeFolder() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));
        when(fileRepository.save(any(FileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FileEntity moved = fileService.moveFile(1L, 1L);

        assertEquals(folder, moved.getFolder());
    }
}
