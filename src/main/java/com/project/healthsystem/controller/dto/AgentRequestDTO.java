package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@Schema(name="Agente")
public class AgentRequestDTO {

    @NotBlank(message = "O nome é obrigatório!")
    @Schema(name = "name")
    private String name;

    @NotBlank(message = "O gênero é obrigatório!")
    @Schema(name = "gender")
    private String gender;

    @NotBlank(message = "O CPF é obrigatório!")
    @CPF(message = "CPF inválido!")
    private String cpf;
    private String phone;
    private String address;

    @NotNull(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

    @NotBlank(message = "O e-mail é obrigatório!")
    @Size(
        max = 320,
        message = "O e-mail não pode ultrapassar 320 caracteres!"
    )
    @Email(message = "Formato de e-mail inválido!")
    @Schema(
        name = "email",
        maxLength = 320,
        description = "Email do agente."
    )
    private String email;

    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
