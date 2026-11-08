package com.project.healthsystem.controller.dto.simplified_info;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ConditionSimplifiedResponseDTO", description = "Dados retornados pela API. ConditionSimplifiedResponseDTO.")
public record ConditionSimplifiedResponseDTO(
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    Long id,
    @Schema(description = "Descrição da condição de saúde cadastrada.")
    String specification
) {
}
