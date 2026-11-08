package com.project.healthsystem.controller.dto.authorization_form;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "AuthorizationFormResponseDTO", description = "Dados retornados pela API. AuthorizationFormResponseDTO.")
public record AuthorizationFormResponseDTO(
    @Schema(description = "Dados do paciente para a autorização.")
    PatientDataResponseDTO patientDataResponseDTO,
    @Schema(description = "Identificação da autorização e destino.")
    AuthorizationIndentifyingResponseDTO authorizationIndentifyingResponseDTO,
    @Schema(description = "Dados do procedimento para a autorização.")
    ProceduringDataResponseDTO proceduringDataResponseDTO,
    @Schema(description = "Descrição textual do registro.")
    String description,
    @Schema(description = "Nome do funcionário responsável.")
    String employeeName,
    @Schema(description = "Data de emissão da autorização; grafia legada mantida. Formato AAAA-MM-DD, por exemplo 2026-11-08.", type = "string", format = "date", example = "1990-05-20")
    LocalDate emitionDate
) {
}
