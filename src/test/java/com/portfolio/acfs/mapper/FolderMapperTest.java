package com.portfolio.acfs.mapper;

import com.portfolio.acfs.dto.FolderDetailResponse;
import com.portfolio.acfs.dto.FolderResponse;
import com.portfolio.acfs.entity.FileEntity;
import com.portfolio.acfs.entity.Folder;
import com.portfolio.acfs.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FolderMapperTest {

    private FolderMapper folderMapper;

    private User user;
    private Folder parentFolder;
    private Folder folder;

    @BeforeEach
    void setUp() {
        folderMapper = Mappers.getMapper(FolderMapper.class);

        user = User.builder()
                .id(1L)
                .email("owner@example.com")
                .build();

        parentFolder = Folder.builder()
                .id(10L)
                .name("Parent")
                .owner(user)
                .children(new ArrayList<>())
                .files(new ArrayList<>())
                .build();

        folder = Folder.builder()
                .id(20L)
                .name("Child")
                .owner(user)
                .parent(parentFolder)
                .children(new ArrayList<>())
                .files(new ArrayList<>())
                .build();

        // Добавляем файлы
        folder.getFiles().add(FileEntity.builder().id(100L).filename("file1.txt").build());
        folder.getFiles().add(FileEntity.builder().id(101L).filename("file2.txt").build());
    }

    @Test
    void toFolderResponse_shouldMapAllFields() {
        FolderResponse response = folderMapper.toFolderResponse(folder);

        assertNotNull(response);
        assertEquals(folder.getId(), response.getId());
        assertEquals(folder.getName(), response.getName());
        assertEquals(user.getId(), response.getOwnerId());
        assertEquals(user.getEmail(), response.getOwnerEmail());
        assertEquals(parentFolder.getId(), response.getParentId());
        assertEquals(folder.getChildren().size(), response.getChildrenCount());
        assertEquals(folder.getFiles().size(), response.getFilesCount());
    }

    @Test
    void toFolderDetailResponse_shouldMapCorrectly() {
        FolderDetailResponse detailResponse = folderMapper.toFolderDetailResponse(folder);

        assertNotNull(detailResponse);
        assertEquals(folder.getId(), detailResponse.getId());
        assertEquals(folder.getName(), detailResponse.getName());
        assertEquals(user.getId(), detailResponse.getOwnerId());
        assertEquals(parentFolder.getId(), detailResponse.getParentId());
    }

    @Test
    void toFolderResponseList_shouldMapList() {
        List<Folder> folders = new ArrayList<>();
        folders.add(folder);

        List<FolderResponse> responses = folderMapper.toFolderResponseList(folders);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(folder.getId(), responses.get(0).getId());
    }
}
