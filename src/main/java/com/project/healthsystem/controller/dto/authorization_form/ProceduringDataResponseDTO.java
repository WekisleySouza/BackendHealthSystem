package com.project.healthsystem.controller.dto.authorization_form;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProceduringDataResponseDTO", description = "Dados retornados pela API. ProceduringDataResponseDTO.")
public record ProceduringDataResponseDTO(
    @Schema(description = "Nome do serviço/procedimento cadastrado.")
    String serviceTypeName,
    @Schema(description = "Classificação do serviço: Exame ou Especialidade. Valores: Exame, Especialidade.", allowableValues = {"Exame", "Especialidade"}, example = "Exame")
    String serviceType,
    @Schema(description = "Código do procedimento usado na autorização.")
    String serviceTypeCode,
    @Schema(description = "Nome da categoria do serviço.")
    String categoryName
) {
}
