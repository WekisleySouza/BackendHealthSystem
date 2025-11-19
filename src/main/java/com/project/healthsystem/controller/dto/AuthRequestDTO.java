package com.project.healthsystem.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {
    @NotBlank(message = "O login deve ser informado!")
    private String login;
    @NotBlank(message = "A senha deve ser informada!")
    private String password;
}
