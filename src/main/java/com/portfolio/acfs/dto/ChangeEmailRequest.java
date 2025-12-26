package com.portfolio.acfs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeEmailRequest {

    @Email
    @NotBlank
    private String newEmail;
}
