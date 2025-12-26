package com.portfolio.acfs.service;

import com.portfolio.acfs.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface FileService {

    FileEntity uploadFile(MultipartFile file, Long folderId) throws IOException;

    FileEntity getFile(Long fileId);

    List<FileEntity> getFilesInFolder(Long folderId);

    List<FileEntity> getUserFiles();

    byte[] downloadFile(Long fileId) throws IOException;

    FileEntity moveFile(Long fileId, Long targetFolderId);

    FileEntity renameFile(Long fileId, String newFilename);

    void deleteFile(Long fileId) throws IOException;
}
