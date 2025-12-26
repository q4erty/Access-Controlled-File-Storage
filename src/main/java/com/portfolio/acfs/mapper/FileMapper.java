package com.portfolio.acfs.mapper;

import com.portfolio.acfs.dto.FileResponse;
import com.portfolio.acfs.dto.FileUploadResponse;
import com.portfolio.acfs.entity.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerEmail", source = "owner.email")
    @Mapping(target = "folderId", source = "folder.id")
    FileResponse toFileResponse(FileEntity file);

    List<FileResponse> toFileResponseList(List<FileEntity> files);

    @Mapping(target = "folderId", source = "folder.id")
    FileUploadResponse toFileUploadResponse(FileEntity file);
}