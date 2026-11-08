package com.project.healthsystem.controller.dto.simplified_info;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EmployeeSimplifiedResponseDTO", description = "Dados retornados pela API. EmployeeSimplifiedResponseDTO.")
public record EmployeeSimplifiedResponseDTO (
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    Long id,
    @Schema(description = "Nome de exibição do registro.")
    String name
) {
}
