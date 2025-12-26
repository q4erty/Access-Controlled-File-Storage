package com.portfolio.acfs.mapper;

import com.portfolio.acfs.dto.PermissionDetailResponse;
import com.portfolio.acfs.dto.PermissionResponse;
import com.portfolio.acfs.dto.PermissionUserResponse;
import com.portfolio.acfs.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "fileId", source = "file.id")
    @Mapping(target = "filename", source = "file.filename")
    @Mapping(target = "type", expression = "java(permission.getType().name())")
    PermissionResponse toPermissionResponse(Permission permission);

    @Mapping(target = "type", expression = "java(permission.getType().name())")
    PermissionDetailResponse toPermissionDetailResponse(Permission permission);

    List<PermissionDetailResponse> toPermissionDetailResponseList(List<Permission> permissions);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "type", expression = "java(permission.getType().name())")
    PermissionUserResponse toPermissionUserResponse(Permission permission);

    List<PermissionUserResponse> toPermissionUserResponseList(List<Permission> permissions);
}