package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "SurgeryTypeResponseDTO", description = "Dados retornados pela API. SurgeryTypeResponseDTO.")
public class SurgeryTypeResponseDTO {
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    private long id;
    @Schema(description = "Descrição livre do tipo de cirurgia; não é enum fechado.")
    private String type;
}
