package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CnesImportResponseDTO", description = "Dados retornados pela API. CnesImportResponseDTO.")
public record CnesImportResponseDTO(
    @Schema(description = "Catálogo substituído na importação. Valores: ESTABLISHMENTS, PROFESSIONALS.", allowableValues = {"ESTABLISHMENTS", "PROFESSIONALS"}, example = "ESTABLISHMENTS")
    String catalog,
    @Schema(description = "Quantidade de registros importados, confirmada após o commit.")
    long importedRecords
) {}
