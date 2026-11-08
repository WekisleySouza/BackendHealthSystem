package com.project.healthsystem.controller.dto.appointment_get_by_id;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CategoryGroupInfoResponseDTO", description = "Dados retornados pela API. CategoryGroupInfoResponseDTO.")
public record CategoryGroupInfoResponseDTO(
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    Long id,
    @Schema(description = "Nome de exibição do registro.")
    String name
) {
}
