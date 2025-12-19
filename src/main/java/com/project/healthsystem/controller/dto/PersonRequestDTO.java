package com.project.healthsystem.controller.dto;

import jakarta.validation.constraints.Email;
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
public class PersonRequestDTO {
    @NotNull(message = "O usuário deve estar atribuído a um agente!")
    private Long agentId;

    private Long responsibleId;

    private List<Long> conditionsId;

    @NotNull(message = "O nome é obrigatório!")
    private String name;

    private String motherName;

    @NotNull(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

    @NotNull(message = "O CNS é obrigatório!")
    private String cns;

    @NotNull(message = "O CPF é obrigatório!")
    @CPF(message = "CPF inválido!")
    private String cpf;

    private String phone;

    @Email(message = "Formato de e-mail inválido!")
    private String email;

    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
