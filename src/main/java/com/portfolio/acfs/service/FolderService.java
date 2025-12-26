package com.portfolio.acfs.service;

import com.portfolio.acfs.entity.Folder;
import java.util.List;

public interface FolderService {

    Folder createFolder(String name, Long parentId);

    Folder getFolderById(Long folderId);

    List<Folder> getRootFolders();

    List<Folder> getSubfolders(Long parentId);

    List<Folder> getUserFolders();

    Folder updateFolder(Long folderId, String name);

    Folder moveFolder(Long folderId, Long newParentId);

    void deleteFolder(Long folderId);
}
