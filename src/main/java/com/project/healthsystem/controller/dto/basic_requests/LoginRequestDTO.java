package com.project.healthsystem.controller.dto.basic_requests;

import com.project.healthsystem.model.annotations.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "A senha é obrigatória!")
    @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres!")
    @StrongPassword
    private String password;
}
