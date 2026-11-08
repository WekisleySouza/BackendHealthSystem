package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PatientInfoResponsibleResponseDTO", description = "Dados retornados pela API. PatientInfoResponsibleResponseDTO.")
public record PatientInfoResponsibleResponseDTO(
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    Long id,
    @Schema(description = "Nome de exibição do registro.")
    String name
) {
}
