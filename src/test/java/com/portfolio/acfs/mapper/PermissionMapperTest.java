package com.portfolio.acfs.mapper;

import com.portfolio.acfs.dto.PermissionDetailResponse;
import com.portfolio.acfs.dto.PermissionResponse;
import com.portfolio.acfs.dto.PermissionUserResponse;
import com.portfolio.acfs.entity.FileEntity;
import com.portfolio.acfs.entity.Permission;
import com.portfolio.acfs.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PermissionMapperTest {

    private PermissionMapper permissionMapper;

    private User user;
    private FileEntity file;
    private Permission permission;

    @BeforeEach
    void setUp() {
        permissionMapper = Mappers.getMapper(PermissionMapper.class);

        user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        file = FileEntity.builder()
                .id(10L)
                .filename("file.txt")
                .build();

        permission = Permission.builder()
                .id(100L)
                .user(user)
                .file(file)
                .type(Permission.PermissionType.READ)
                .build();
    }

    @Test
    void toPermissionResponse_shouldMapAllFields() {
        PermissionResponse response = permissionMapper.toPermissionResponse(permission);

        assertNotNull(response);
        assertEquals(permission.getId(), response.getId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getEmail(), response.getUserEmail());
        assertEquals(file.getId(), response.getFileId());
        assertEquals(file.getFilename(), response.getFilename());
        assertEquals(permission.getType().name(), response.getType());
    }

    @Test
    void toPermissionDetailResponse_shouldMapCorrectly() {
        PermissionDetailResponse detailResponse = permissionMapper.toPermissionDetailResponse(permission);

        assertNotNull(detailResponse);
        assertEquals(permission.getId(), detailResponse.getId());
        assertEquals(permission.getType().name(), detailResponse.getType());
    }

    @Test
    void toPermissionDetailResponseList_shouldMapList() {
        List<PermissionDetailResponse> list = permissionMapper.toPermissionDetailResponseList(List.of(permission));

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(permission.getId(), list.get(0).getId());
    }

    @Test
    void toPermissionUserResponse_shouldMapAllFields() {
        PermissionUserResponse userResponse = permissionMapper.toPermissionUserResponse(permission);

        assertNotNull(userResponse);
        assertEquals(user.getId(), userResponse.getUserId());
        assertEquals(user.getEmail(), userResponse.getUserEmail());
        assertEquals(permission.getType().name(), userResponse.getType());
    }

    @Test
    void toPermissionUserResponseList_shouldMapList() {
        List<PermissionUserResponse> list = permissionMapper.toPermissionUserResponseList(List.of(permission));

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(user.getId(), list.get(0).getUserId());
    }
}
