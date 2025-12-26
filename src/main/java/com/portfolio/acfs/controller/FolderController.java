package com.portfolio.acfs.controller;

import com.portfolio.acfs.dto.*;
import com.portfolio.acfs.entity.Folder;
import com.portfolio.acfs.mapper.FolderMapper;
import com.portfolio.acfs.service.impl.FolderServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderServiceImpl folderService;
    private final FolderMapper folderMapper;

    @PostMapping
    public ResponseEntity<FolderDetailResponse> createFolder(@Valid @RequestBody CreateFolderRequest request) {
        Folder folder = folderService.createFolder(request.getName(), request.getParentId());
        return ResponseEntity.ok(folderMapper.toFolderDetailResponse(folder));
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<FolderResponse> getFolder(@PathVariable Long folderId) {
        Folder folder = folderService.getFolderById(folderId);
        return ResponseEntity.ok(folderMapper.toFolderResponse(folder));
    }

    @GetMapping
    public ResponseEntity<List<FolderResponse>> getRootFolders() {
        List<Folder> folders = folderService.getRootFolders();
        return ResponseEntity.ok(folderMapper.toFolderResponseList(folders));
    }

    @GetMapping("/{folderId}/subfolders")
    public ResponseEntity<List<FolderResponse>> getSubfolders(@PathVariable Long folderId) {
        List<Folder> folders = folderService.getSubfolders(folderId);
        return ResponseEntity.ok(folderMapper.toFolderResponseList(folders));
    }

    @GetMapping("/my")
    public ResponseEntity<List<FolderResponse>> getMyFolders() {
        List<Folder> folders = folderService.getUserFolders();
        return ResponseEntity.ok(folderMapper.toFolderResponseList(folders));
    }

    @PutMapping("/{folderId}")
    public ResponseEntity<FolderDetailResponse> updateFolder(
            @PathVariable Long folderId,
            @Valid @RequestBody UpdateFolderRequest request) {
        Folder folder = folderService.updateFolder(folderId, request.getName());
        return ResponseEntity.ok(folderMapper.toFolderDetailResponse(folder));
    }

    @PutMapping("/{folderId}/move")
    public ResponseEntity<FolderDetailResponse> moveFolder(
            @PathVariable Long folderId,
            @Valid @RequestBody MoveFolderRequest request) {
        Folder folder = folderService.moveFolder(folderId, request.getNewParentId());
        return ResponseEntity.ok(folderMapper.toFolderDetailResponse(folder));
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<MessageResponse> deleteFolder(@PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
        return ResponseEntity.ok(new MessageResponse("Folder deleted successfully"));
    }
}