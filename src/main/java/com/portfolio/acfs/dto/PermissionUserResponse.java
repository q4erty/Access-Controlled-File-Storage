package com.portfolio.acfs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionUserResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String type;
}