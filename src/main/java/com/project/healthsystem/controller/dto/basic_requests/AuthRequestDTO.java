package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

@Schema(name = "AuthRequestDTO", description = "Dados enviados pelo frontend. AuthRequestDTO.")
public class AuthRequestDTO {
    @NotBlank(message = "O login deve ser informado!")
    @Size(max = 100, message = "O login não pode ultrapassar 100 caracteres!")
    
    @Schema(description = "Identificação de acesso do usuário.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    private String login;
    @NotBlank(message = "A senha deve ser informada!")
    @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres!")
    
    @Schema(description = "Senha de acesso; envie somente na requisição, nunca em logs.", requiredMode = Schema.RequiredMode.REQUIRED, format = "password", accessMode = Schema.AccessMode.WRITE_ONLY, minLength = 6, maxLength = 72)
    private String password;
}
