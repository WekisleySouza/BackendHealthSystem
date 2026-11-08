package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Ciap2ResponseDTO", description = "Dados retornados pela API. Ciap2ResponseDTO.")
public class Ciap2ResponseDTO {
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
    @Schema(description = "Indicador de filtro padrão na fonte PEC; pode ser null e não comprova elegibilidade na RNDS.", example = "false")
    private Boolean defaultFilterInPec;
}
