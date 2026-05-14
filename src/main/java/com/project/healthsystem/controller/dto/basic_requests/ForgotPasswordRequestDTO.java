package com.project.healthsystem.controller.dto.basic_requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDTO {
    @NotBlank(message = "O email é obrigatório!")
    private String email;
}
