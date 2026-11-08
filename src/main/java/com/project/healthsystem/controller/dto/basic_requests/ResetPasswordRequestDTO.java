package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "ResetPasswordRequestDTO", description = "Dados enviados pelo frontend. ResetPasswordRequestDTO.")
public class ResetPasswordRequestDTO {
    @NotBlank(message = "O token é obrigatório!")
    @Schema(description = "Token recebido no fluxo de redefinição de senha.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;
    @NotBlank(message = "A nova senha é obrigatória!")
    @Schema(description = "Nova senha para concluir a redefinição.", requiredMode = Schema.RequiredMode.REQUIRED, format = "password", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String newPassword;
}
