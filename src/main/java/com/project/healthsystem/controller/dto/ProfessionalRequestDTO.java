package com.project.healthsystem.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalRequestDTO {

    @NotBlank(message = "O nome é obrigatório!")
    private String name;

    @NotNull(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

    @NotBlank(message = "O CPF é obrigatório!")
    @CPF(message = "CPF inválido!")
    private String cpf;

    private String phone;

    @Size(max = 320, message = "O e-mail não pode ultrapassar 320 caracteres!")
    @Email(message = "Formato de e-mail inválido!")
    private String email;
}
