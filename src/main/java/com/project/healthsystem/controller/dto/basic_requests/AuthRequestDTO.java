package com.project.healthsystem.controller.dto.basic_requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequestDTO {
    @NotBlank(message = "O login deve ser informado!")
    @Size(max = 100, message = "O login não pode ultrapassar 100 caracteres!")
    private String login;
    @NotBlank(message = "A senha deve ser informada!")
    @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres!")
    private String password;
}
