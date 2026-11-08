package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import com.project.healthsystem.model.annotations.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "LoginRequestDTO", description = "Dados enviados pelo frontend. LoginRequestDTO.")
public class LoginRequestDTO {
    @NotBlank(message = "A senha é obrigatória!")
    @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres!")
    @StrongPassword
    @Schema(description = "Senha de acesso; envie somente na requisição, nunca em logs.", requiredMode = Schema.RequiredMode.REQUIRED, format = "password", accessMode = Schema.AccessMode.WRITE_ONLY, minLength = 6, maxLength = 72)
    private String password;
}
