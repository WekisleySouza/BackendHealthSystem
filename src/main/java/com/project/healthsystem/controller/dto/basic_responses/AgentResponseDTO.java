package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@Schema(name = "AgentResponseDTO", description = "Dados retornados pela API. AgentResponseDTO.")
public class AgentResponseDTO {
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    private long id;
    @Schema(description = "Nome de exibição do registro.")
    private String name;
    @Schema(description = "Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.")
    private String gender;
    @Schema(description = "Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.")
    private String sex;
    @Schema(description = "Telefone celular de contato.")
    private String cellPhone;
    @Schema(description = "Telefone residencial.")
    private String residentialPhone;
    @Schema(description = "Telefone de contato alternativo.")
    private String contactPhone;
    @Schema(description = "CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.")
    private String cpf;
    @Schema(description = "Endereço completo informado no cadastro.")
    private String address;
    @Schema(description = "Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08.", type = "string", format = "date", example = "1990-05-20")
    private LocalDate birthday;
    @Schema(description = "Endereço de e-mail de contato.", example = "usuario@example.com", format = "email")
    private String email;
}
