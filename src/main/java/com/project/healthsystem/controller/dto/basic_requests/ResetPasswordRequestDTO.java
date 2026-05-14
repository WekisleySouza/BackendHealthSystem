package com.project.healthsystem.controller.dto.basic_requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    @NotBlank(message = "O token é obrigatório!")
    private String token;
    @NotBlank(message = "A nova senha é obrigatória!")
    private String newPassword;
}
