package com.portfolio.acfs.controller;

import com.portfolio.acfs.dto.*;
import com.portfolio.acfs.entity.FileEntity;
import com.portfolio.acfs.mapper.FileMapper;
import com.portfolio.acfs.service.impl.FileServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileServiceImpl fileService;
    private final FileMapper fileMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderId", required = false) Long folderId) throws IOException {

        FileEntity fileEntity = fileService.uploadFile(file, folderId);
        return ResponseEntity.ok(fileMapper.toFileUploadResponse(fileEntity));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<FileResponse> getFileMetadata(@PathVariable Long fileId) {
        FileEntity file = fileService.getFile(fileId);
        return ResponseEntity.ok(fileMapper.toFileResponse(file));
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long fileId) throws IOException {
        FileEntity file = fileService.getFile(fileId);
        byte[] data = fileService.downloadFile(fileId);

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(data.length)
                .body(resource);
    }

    @GetMapping
    public ResponseEntity<List<FileResponse>> getFilesInFolder(
            @RequestParam(required = false) Long folderId) {
        List<FileEntity> files = fileService.getFilesInFolder(folderId);
        return ResponseEntity.ok(fileMapper.toFileResponseList(files));
    }

    @GetMapping("/my")
    public ResponseEntity<List<FileResponse>> getMyFiles() {
        List<FileEntity> files = fileService.getUserFiles();
        return ResponseEntity.ok(fileMapper.toFileResponseList(files));
    }

    @PutMapping("/{fileId}/move")
    public ResponseEntity<FileUploadResponse> moveFile(
            @PathVariable Long fileId,
            @Valid @RequestBody MoveFileRequest request) {
        FileEntity file = fileService.moveFile(fileId, request.getFolderId());
        return ResponseEntity.ok(fileMapper.toFileUploadResponse(file));
    }

    @PutMapping("/{fileId}/rename")
    public ResponseEntity<FileUploadResponse> renameFile(
            @PathVariable Long fileId,
            @Valid @RequestBody RenameFileRequest request) {
        FileEntity file = fileService.renameFile(fileId, request.getFilename());
        return ResponseEntity.ok(fileMapper.toFileUploadResponse(file));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<MessageResponse> deleteFile(@PathVariable Long fileId) throws IOException {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok(new MessageResponse("File deleted successfully"));
    }
}