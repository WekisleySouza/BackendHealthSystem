package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.project.healthsystem.controller.dto.basic_responses.ConditionResponseDTO;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "PatientInfoResponseDTO", description = "Dados retornados pela API. PatientInfoResponseDTO.")
public record PatientInfoResponseDTO(
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    long id,
    @Schema(description = "Nome da equipe de saúde.")
    String teamName,
    @Schema(description = "Identificador Nacional de Equipe (INE), mantido como texto.")
    String teamINE,
    @Schema(description = "Identificação da microárea de acompanhamento.")
    String microArea,
    @Schema(description = "Origem do cadastro ou encaminhamento.")
    String origin,
    @Schema(description = "Dados resumidos do agente de saúde vinculado.")
    PatientInfoAgentResponseDTO agent,
    @Schema(description = "Dados resumidos do responsável pelo paciente.")
    PatientInfoResponsibleResponseDTO responsible,
    @Schema(description = "Condições associadas ao paciente.")
    List<ConditionResponseDTO> conditions,
    @Schema(description = "Atendimentos vinculados ao paciente.")
    List<PatientInfoAppointmentResponseDTO> appointments,
    @Schema(description = "Nome de exibição do registro.")
    String name,
    @Schema(description = "Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.")
    String gender,
    @Schema(description = "Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.")
    String sex,
    @Schema(description = "Nome da mãe do paciente.")
    String motherName,
    @Schema(description = "Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08.", type = "string", format = "date", example = "1990-05-20")
    LocalDate birthday,
    @Schema(description = "Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda.", example = "000000000000001")
    String cns,
    @Schema(description = "CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.")
    String cpf,
    @Schema(description = "Endereço completo informado no cadastro.")
    String address,
    @Schema(description = "Telefone celular de contato.")
    String cellPhone,
    @Schema(description = "Telefone residencial.")
    String residentialPhone,
    @Schema(description = "Telefone de contato alternativo.")
    String contactPhone,
    @Schema(description = "Endereço de e-mail de contato.", example = "usuario@example.com", format = "email")
    String email
) {
}
