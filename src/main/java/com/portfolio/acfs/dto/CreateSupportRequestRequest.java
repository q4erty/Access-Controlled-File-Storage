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
public class CreateSupportRequestRequest {
    @NotBlank(message = "Subject is required")
    @Size(min = 3, max = 255, message = "Subject must be between 3 and 255 characters")
    private String subject;

    @NotBlank(message = "Question is required")
    @Size(min = 10, message = "Question must be at least 10 characters")
    private String question;
}