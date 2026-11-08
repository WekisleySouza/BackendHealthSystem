package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PatientInfoAgentResponseDTO", description = "Dados retornados pela API. PatientInfoAgentResponseDTO.")
public record PatientInfoAgentResponseDTO (
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    Long id,
    @Schema(description = "Nome de exibição do registro.")
    String name
){}
