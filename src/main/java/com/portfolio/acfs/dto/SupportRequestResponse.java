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
public class SupportRequestResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String subject;
    private String question;
    private String answer;
    private String status;
    private Long answeredById;
    private String answeredByEmail;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
}