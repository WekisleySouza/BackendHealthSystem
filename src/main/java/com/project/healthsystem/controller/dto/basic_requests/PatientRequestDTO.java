package com.project.healthsystem.controller.dto.basic_requests;

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
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDTO {
    // Only report fields
    @Size(max = 150, message = "O nome da equipe não pode ultrapassar 150 caracteres!")
    private String teamName;
    @Size(max = 20, message = "O INE não pode ultrapassar 20 caracteres!")
    private String teamINE;
    @Size(max = 50, message = "A microárea não pode ultrapassar 50 caracteres!")
    private String microArea;
    @Size(max = 100, message = "A origem não pode ultrapassar 100 caracteres!")
    private String origin;

    // System fields
    private Long agentId;

    private Long responsibleId;

    private List<Long> conditionsId;

    @NotNull(message = "O nome é obrigatório!")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    private String name;

    @NotBlank(message = "O sexo é obrigatório!")
    @Size(max = 20, message = "O sexo não pode ultrapassar 20 caracteres!")
    @Schema(name = "sexo")
    private String sex;
    @NotBlank(message = "O gênero é obrigatório!")
    @Size(max = 50, message = "O gênero não pode ultrapassar 50 caracteres!")
    @Schema(name = "gênero")
    private String gender;
    @Size(max = 20, message = "O telefone celular não pode ultrapassar 20 caracteres!")
    private String cellPhone;
    @Size(max = 20, message = "O telefone residencial não pode ultrapassar 20 caracteres!")
    private String residentialPhone;
    @Size(max = 20, message = "O telefone de contato não pode ultrapassar 20 caracteres!")
    private String contactPhone;

    @Size(max = 150, message = "O nome da mãe não pode ultrapassar 150 caracteres!")
    private String motherName;

    @NotNull(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

    @Size(max = 20, message = "O CNS não pode ultrapassar 20 caracteres!")
    private String cns;

    @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres!")
    private String cpf;

    @Size(max = 255, message = "O endereço não pode ultrapassar 255 caracteres!")
    private String address;

    @Size(
            max = 320,
            message = "O e-mail não pode ultrapassar 320 caracteres!"
    )
    @Email(message = "Formato de e-mail inválido!")
    private String email;

    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
