package com.portfolio.acfs.mapper;

import com.portfolio.acfs.dto.FolderDetailResponse;
import com.portfolio.acfs.dto.FolderResponse;
import com.portfolio.acfs.entity.Folder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FolderMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerEmail", source = "owner.email")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "childrenCount", expression = "java(folder.getChildren().size())")
    @Mapping(target = "filesCount", expression = "java(folder.getFiles().size())")
    FolderResponse toFolderResponse(Folder folder);

    List<FolderResponse> toFolderResponseList(List<Folder> folders);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "parentId", source = "parent.id")
    FolderDetailResponse toFolderDetailResponse(Folder folder);
}