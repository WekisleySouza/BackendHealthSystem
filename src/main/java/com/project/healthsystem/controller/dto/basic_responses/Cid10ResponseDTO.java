package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Cid10ResponseDTO", description = "Dados retornados pela API. Cid10ResponseDTO.")
public class Cid10ResponseDTO {
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    private Long id;
    @Schema(description = "Código oficial do item no catálogo, mantido como texto.")
    private String code;
    @Schema(description = "Descrição textual do registro.")
    private String description;
    @Schema(description = "Identificador interno do registro no PEC; não é o código clínico.")
    private Long pecId;
    @Schema(description = "Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.")
    private String sex;
    @Schema(description = "Indica se o código estava ativo na fonte PEC importada; a consulta também retorna inativos.", example = "false")
    private Boolean activeInPec;
}
