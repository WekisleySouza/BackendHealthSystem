package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Agent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class AgentDTO {

    private long id;
    @NotBlank(message = "O nome é obrigatório!")
    private String name;

    @NotBlank(message = "O CPF é obrigatório!")
    @CPF(message = "CPF inválido!")
    private String cpf;

    private String phone;

    @NotNull(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

    @NotBlank(message = "O e-mail é obrigatório!")
    @Size(
            max = 320,
            message = "O e-mail não pode ultrapassar 320 caracteres!"
    )
    @Email(message = "Formato de e-mail inválido!")
    private String email;

}
