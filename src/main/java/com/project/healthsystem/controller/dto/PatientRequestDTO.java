package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
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
    @NotNull(message = "O nome do time é obrigatório!")
    private String teamName;
    @NotNull(message = "O INE do time é obrigatório!")
    private String teamINE;
    @NotNull(message = "A micro área é obrigatória!")
    private String microArea;
    @NotNull(message = "A origem é obrigatória!")
    private String origin;

    // System fields
    @NotNull(message = "O usuário deve estar atribuído a um agente!")
    private Long agentId;

    private Long responsibleId;

    private List<Long> conditionsId;

    @NotNull(message = "O nome é obrigatório!")
    private String name;

    @NotBlank(message = "O sexo é obrigatório!")
    @Schema(name = "sexo")
    private String sex;
    private String gender;
    private String cellPhone;
    private String residentialPhone;
    private String contactPhone;

    private String motherName;

    @NotNull(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

    @NotNull(message = "O CNS é obrigatório!")
    private String cns;

    @NotNull(message = "O CPF é obrigatório!")
    @CPF(message = "CPF inválido!")
    private String cpf;

    private String address;
    private String phone;

    @Email(message = "Formato de e-mail inválido!")
    private String email;

    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
