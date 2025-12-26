package com.portfolio.acfs.mapper;

import com.portfolio.acfs.dto.FileResponse;
import com.portfolio.acfs.dto.FileUploadResponse;
import com.portfolio.acfs.entity.FileEntity;
import com.portfolio.acfs.entity.Folder;
import com.portfolio.acfs.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileMapperTest {

    private FileMapper fileMapper;

    private User user;
    private Folder folder;
    private FileEntity file;

    @BeforeEach
    void setUp() {
        fileMapper = Mappers.getMapper(FileMapper.class);

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        folder = Folder.builder()
                .id(10L)
                .name("Documents")
                .build();

        file = FileEntity.builder()
                .id(100L)
                .filename("file.txt")
                .contentType("text/plain")
                .size(1024L)
                .owner(user)
                .folder(folder)
                .build();
    }

    @Test
    void toFileResponse_shouldMapAllFields() {
        FileResponse response = fileMapper.toFileResponse(file);

        assertNotNull(response);
        assertEquals(file.getId(), response.getId());
        assertEquals(file.getFilename(), response.getFilename());
        assertEquals(file.getContentType(), response.getContentType());
        assertEquals(file.getSize(), response.getSize());
        assertEquals(user.getId(), response.getOwnerId());
        assertEquals(user.getEmail(), response.getOwnerEmail());
        assertEquals(folder.getId(), response.getFolderId());
    }

    @Test
    void toFileUploadResponse_shouldMapFolderId() {
        FileUploadResponse response = fileMapper.toFileUploadResponse(file);

        assertNotNull(response);
        assertEquals(file.getId(), response.getId());
        assertEquals(file.getFilename(), response.getFilename());
        assertEquals(folder.getId(), response.getFolderId());
    }

    @Test
    void toFileResponseList_shouldMapList() {
        List<FileEntity> files = List.of(file);
        List<FileResponse> responses = fileMapper.toFileResponseList(files);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(file.getId(), responses.get(0).getId());
    }
}
