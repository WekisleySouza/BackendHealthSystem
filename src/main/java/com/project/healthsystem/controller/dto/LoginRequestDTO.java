package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.annotations.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "A senha é obrigatória!")
    @StrongPassword
    private String password;
}
