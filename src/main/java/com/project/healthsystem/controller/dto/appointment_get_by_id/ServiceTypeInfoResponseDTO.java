package com.project.healthsystem.controller.dto.appointment_get_by_id;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ServiceTypeInfoResponseDTO", description = "Dados retornados pela API. ServiceTypeInfoResponseDTO.")
public record ServiceTypeInfoResponseDTO(
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    Long id,
    @Schema(description = "Tipo/classificação do registro. Valores: Exame, Especialidade.", allowableValues = {"Exame", "Especialidade"}, example = "Exame")
    String type,
    @Schema(description = "Nome de exibição do registro.")
    String name
){}

