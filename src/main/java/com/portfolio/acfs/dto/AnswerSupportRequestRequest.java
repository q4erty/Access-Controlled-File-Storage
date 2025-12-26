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
public class AnswerSupportRequestRequest {
    @NotBlank(message = "Answer is required")
    @Size(min = 10, message = "Answer must be at least 10 characters")
    private String answer;
}