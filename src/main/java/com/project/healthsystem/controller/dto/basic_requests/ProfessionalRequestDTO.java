package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProfessionalRequestDTO", description = "Dados enviados pelo frontend. ProfessionalRequestDTO.")
public class ProfessionalRequestDTO {

    // Fields accordingly with SNES:
    // aDD CNS, CBO(OBG), VINCULAÇÃO, DESCRIÇÃO.
    @Size(max = 20, message = "O CNS não pode ultrapassar 20 caracteres!")
    @Schema(description = "Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "000000000000001", maxLength = 20, nullable = true)
    private String cns;
    @Size(max = 10, message = "O CBO não pode ultrapassar 10 caracteres!")
    @NotBlank(message = "O cbo é obrigatório!")
    @Schema(description = "Código da Classificação Brasileira de Ocupações, mantido como texto.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 10)
    private String cbo;
    @Size(max = 50, message = "O vínculo não pode ultrapassar 50 caracteres!")
    @Schema(description = "Descrição do vínculo do profissional.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 50, nullable = true)
    private String vinculation;
    @Size(max = 255, message = "A descrição não pode ultrapassar 255 caracteres!")
    @Schema(description = "Descrição textual do registro.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 255, nullable = true)
    private String description;

    // Existent Fields:
    @NotBlank(message = "O nome é obrigatório!")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    @Schema(description = "Nome de exibição do registro.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 150)
    private String name;

    @NotBlank(message = "O sexo é obrigatório!")
    @Size(max = 20, message = "O sexo não pode ultrapassar 20 caracteres!")
    @Schema(description = "Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 20)
    private String sex;
    @NotBlank(message = "O gênero é obrigatório!")
    @Size(max = 50, message = "O gênero não pode ultrapassar 50 caracteres!")
    @Schema(description = "Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    private String gender;
    @Size(max = 20, message = "O telefone celular não pode ultrapassar 20 caracteres!")
    @Schema(description = "Telefone celular de contato.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 20, nullable = true)
    private String cellPhone;
    @Size(max = 20, message = "O telefone residencial não pode ultrapassar 20 caracteres!")
    @Schema(description = "Telefone residencial.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 20, nullable = true)
    private String residentialPhone;
    @Size(max = 20, message = "O telefone de contato não pode ultrapassar 20 caracteres!")
    @Schema(description = "Telefone de contato alternativo.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 20, nullable = true)
    private String contactPhone;

    @Schema(description = "Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", format = "date", example = "1990-05-20", nullable = true)
    private LocalDate birthday;
    @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres!")
    @Schema(description = "CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 14, nullable = true)
    private String cpf;

    @Size(max = 255, message = "O endereço não pode ultrapassar 255 caracteres!")
    @Schema(description = "Endereço completo informado no cadastro.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 255, nullable = true)
    private String address;

    @Size(max = 320, message = "O e-mail não pode ultrapassar 320 caracteres!")
    @Email(message = "Formato de e-mail inválido!")
    @Schema(description = "Endereço de e-mail de contato.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "usuario@example.com", format = "email", maxLength = 320, nullable = true)
    private String email;

    @Schema(description = "CPF normalizado calculado pelo backend; não envie este campo.", accessMode = Schema.AccessMode.READ_ONLY)
    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
