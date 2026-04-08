package com.project.healthsystem.controller.dto.basic_requests;

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
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    @Schema(name = "name")
    private String name;

    @NotBlank(message = "O sexo é obrigatório!")
    @Size(max = 20, message = "O sexo não pode ultrapassar 20 caracteres!")
    @Schema(name = "sex")
    private String sex;
    @NotBlank(message = "O gênero é obrigatório!")
    @Size(max = 50, message = "O gênero não pode ultrapassar 50 caracteres!")
    @Schema(name = "gender")
    private String gender;
    @Size(max = 20, message = "O telefone celular não pode ultrapassar 20 caracteres!")
    private String cellPhone;
    @Size(max = 20, message = "O telefone residencial não pode ultrapassar 20 caracteres!")
    private String residentialPhone;
    @Size(max = 20, message = "O telefone de contato não pode ultrapassar 20 caracteres!")
    private String contactPhone;


    @NotBlank(message = "O CPF é obrigatório!")
    @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres!")
    @CPF(message = "CPF inválido!")
    private String cpf;
    @Size(max = 255, message = "O endereço não pode ultrapassar 255 caracteres!")
    private String address;

    @NotNull(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

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
