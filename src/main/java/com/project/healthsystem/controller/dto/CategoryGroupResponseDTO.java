package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

@Schema(name = "CategoryGroupResponseDTO", description = "Dados retornados pela API. CategoryGroupResponseDTO.")
public class CategoryGroupResponseDTO {
    
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    private long id;
    
    @Schema(description = "Nome de exibição do registro.")
    private String name;
}
