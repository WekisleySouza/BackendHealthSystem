package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ConditionResponseDTO", description = "Dados retornados pela API. ConditionResponseDTO.")
public class ConditionResponseDTO {
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    private long id;
    @Schema(description = "Descrição da condição de saúde cadastrada.")
    private String specification;
}
