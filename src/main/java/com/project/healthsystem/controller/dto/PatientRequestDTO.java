package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDTO {
    // Only report fields
    private String teamName;
    private String teamINE;
    private String microArea;
    private String origin;

    // System fields
    private Long agentId;

    private Long responsibleId;

    private List<Long> conditionsId;

    @NotNull(message = "O nome é obrigatório!")
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

    private String motherName;

    @NotNull(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

    private String cns;

    @CPF(message = "CPF inválido!")
    private String cpf;

    private String address;

    @Email(message = "Formato de e-mail inválido!")
    private String email;

    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
