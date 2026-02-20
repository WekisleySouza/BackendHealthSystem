package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    // Fields accordingly with SNES:
    // aDD CNS, CBO(OBG), VINCULAÇÃO, DESCRIÇÃO.
    private String cns;
    @NotBlank(message = "O cbo é obrigatório!")
    private String cbo;
    private String vinculation;
    private String description;

    // Existent Fields:
    @NotBlank(message = "O nome é obrigatório!")
    private String name;

    @NotBlank(message = "O sexo é obrigatório!")
    @Schema(name = "sexo")
    private String sex;
    @NotBlank(message = "O gênero é obrigatório!")
    @Schema(name = "gênero")
    private String gender;
    private String cellPhone;
    private String residentialPhone;
    private String contactPhone;

    private LocalDate birthday;

    @CPF(message = "CPF inválido!")
    private String cpf;

    private String address;
    private String phone;

    @Size(max = 320, message = "O e-mail não pode ultrapassar 320 caracteres!")
    @Email(message = "Formato de e-mail inválido!")
    private String email;

    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
