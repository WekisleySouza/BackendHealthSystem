package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

@Schema(name = "ForgotPasswordRequestDTO", description = "Dados enviados pelo frontend. ForgotPasswordRequestDTO.")
public class ForgotPasswordRequestDTO {
    @NotBlank(message = "O email é obrigatório!")
    
    @Schema(description = "Endereço de e-mail de contato.", requiredMode = Schema.RequiredMode.REQUIRED, example = "usuario@example.com", format = "email")
    private String email;
}
