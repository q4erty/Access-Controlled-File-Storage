package com.portfolio.acfs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private Long id;
    private String filename;
    private Long size;
    private String contentType;
    private Long ownerId;
    private String ownerEmail;
    private Long folderId;
    private LocalDateTime uploadedAt;
}