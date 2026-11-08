package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AgreementResponseDTO", description = "Dados retornados pela API. AgreementResponseDTO.")
public class AgreementResponseDTO {
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    private Long id;
    @Schema(description = "Nome de exibição do registro.")
    private String name;
}
