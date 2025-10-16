package com.project.healthsystem.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginDTO {

    private long id;
    private long userId;

    @NotBlank(message = "A senha é obrigatória!")
    private String password;

    @NotBlank(message = "O papel (role) é obrigatório!")
    private String role;
}
