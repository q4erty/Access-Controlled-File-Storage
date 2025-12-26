package com.portfolio.acfs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenameFileRequest {
    @NotBlank(message = "Filename is required")
    @Size(min = 1, max = 255, message = "Filename must be between 1 and 255 characters")
    private String filename;
}
