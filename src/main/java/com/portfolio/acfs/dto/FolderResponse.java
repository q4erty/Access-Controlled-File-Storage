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
public class FolderResponse {
    private Long id;
    private String name;
    private Long ownerId;
    private String ownerEmail;
    private Long parentId;
    private LocalDateTime createdAt;
    private Integer childrenCount;
    private Integer filesCount;
}