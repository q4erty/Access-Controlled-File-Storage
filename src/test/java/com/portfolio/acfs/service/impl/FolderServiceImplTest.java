package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.entity.Folder;
import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.repository.FolderRepository;
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

class FolderServiceImplTest {

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private FolderServiceImpl folderService;

    private User user;
    private Folder parentFolder;
    private Folder folder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .roles(Set.of(Role.builder().name(Role.RoleType.USER).build()))
                .build();

        parentFolder = Folder.builder()
                .id(1L)
                .name("Parent")
                .owner(user)
                .build();

        folder = Folder.builder()
                .id(2L)
                .name("Child")
                .owner(user)
                .parent(parentFolder)
                .build();
    }

    @Test
    void createFolderShouldSaveFolder() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(parentFolder));
        when(folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Folder created = folderService.createFolder("NewFolder", 1L);

        assertNotNull(created);
        assertEquals("NewFolder", created.getName());
        assertEquals(parentFolder, created.getParent());
        assertEquals(user, created.getOwner());
        verify(folderRepository).save(any(Folder.class));
    }

    @Test
    void getFolderByIdShouldReturnFolderIfOwner() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(folderRepository.findById(2L)).thenReturn(Optional.of(folder));

        Folder found = folderService.getFolderById(2L);

        assertNotNull(found);
        assertEquals(folder.getId(), found.getId());
    }

    @Test
    void getFolderByIdShouldThrowIfNotOwner() {
        User otherUser = User.builder().id(3L).build();
        when(userService.getCurrentUser()).thenReturn(otherUser);
        when(folderRepository.findById(2L)).thenReturn(Optional.of(folder));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> folderService.getFolderById(2L));
        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void updateFolderShouldChangeName() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(folderRepository.findById(2L)).thenReturn(Optional.of(folder));
        when(folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Folder updated = folderService.updateFolder(2L, "UpdatedName");

        assertEquals("UpdatedName", updated.getName());
        verify(folderRepository).save(any(Folder.class));
    }

    @Test
    void moveFolderShouldChangeParent() {
        Folder newParent = Folder.builder().id(3L).owner(user).build();
        when(userService.getCurrentUser()).thenReturn(user);
        when(folderRepository.findById(2L)).thenReturn(Optional.of(folder));
        when(folderRepository.findById(3L)).thenReturn(Optional.of(newParent));
        when(folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Folder moved = folderService.moveFolder(2L, 3L);

        assertEquals(newParent, moved.getParent());
    }

    @Test
    void deleteFolderShouldCallRepositoryDelete() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(folderRepository.findById(2L)).thenReturn(Optional.of(folder));

        folderService.deleteFolder(2L);

        verify(folderRepository).delete(folder);
    }

    @Test
    void getSubfoldersShouldReturnList() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(parentFolder));
        when(folderRepository.findByParentId(1L)).thenReturn(List.of(folder));

        List<Folder> subfolders = folderService.getSubfolders(1L);

        assertEquals(1, subfolders.size());
        assertEquals(folder, subfolders.get(0));
    }
}
