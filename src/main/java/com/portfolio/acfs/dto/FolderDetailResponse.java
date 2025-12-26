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
public class FolderDetailResponse {
    private Long id;
    private String name;
    private Long ownerId;
    private Long parentId;
    private LocalDateTime createdAt;
}