package com.project.healthsystem.controller.dto.basic_requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalRequestDTO {

    // Fields accordingly with SNES:
    // aDD CNS, CBO(OBG), VINCULAÇÃO, DESCRIÇÃO.
    @Size(max = 20, message = "O CNS não pode ultrapassar 20 caracteres!")
    private String cns;
    @Size(max = 10, message = "O CBO não pode ultrapassar 10 caracteres!")
    @NotBlank(message = "O cbo é obrigatório!")
    private String cbo;
    @Size(max = 50, message = "O vínculo não pode ultrapassar 50 caracteres!")
    private String vinculation;
    @Size(max = 255, message = "A descrição não pode ultrapassar 255 caracteres!")
    private String description;

    // Existent Fields:
    @NotBlank(message = "O nome é obrigatório!")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    private String name;

    @NotBlank(message = "O sexo é obrigatório!")
    @Size(max = 20, message = "O sexo não pode ultrapassar 20 caracteres!")
    private String sex;
    @NotBlank(message = "O gênero é obrigatório!")
    @Size(max = 50, message = "O gênero não pode ultrapassar 50 caracteres!")
    private String gender;
    @Size(max = 20, message = "O telefone celular não pode ultrapassar 20 caracteres!")
    private String cellPhone;
    @Size(max = 20, message = "O telefone residencial não pode ultrapassar 20 caracteres!")
    private String residentialPhone;
    @Size(max = 20, message = "O telefone de contato não pode ultrapassar 20 caracteres!")
    private String contactPhone;

    private LocalDate birthday;
    @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres!")
    private String cpf;

    @Size(max = 255, message = "O endereço não pode ultrapassar 255 caracteres!")
    private String address;

    @Size(max = 320, message = "O e-mail não pode ultrapassar 320 caracteres!")
    @Email(message = "Formato de e-mail inválido!")
    private String email;

    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
