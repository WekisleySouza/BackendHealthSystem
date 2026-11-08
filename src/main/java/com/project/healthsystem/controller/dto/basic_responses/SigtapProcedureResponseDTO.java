package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "SigtapProcedureResponseDTO", description = "Dados retornados pela API. SigtapProcedureResponseDTO.")
public class SigtapProcedureResponseDTO {
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    private Long id;
    @Schema(description = "Código oficial do item no catálogo, mantido como texto.")
    private String code;
    @Schema(description = "Nome de exibição do registro.")
    private String name;
    @Schema(description = "Competência da base, no formato AAAAMM.", example = "202607", pattern = "^[0-9]{6}$")
    private String competence;
    @Schema(description = "Código de complexidade conforme a fonte SIGTAP.")
    private String complexity;
    @Schema(description = "Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.")
    private String sex;
    @Schema(description = "Idade mínima aplicável conforme a fonte SIGTAP.")
    private Integer minimumAge;
    @Schema(description = "Idade máxima aplicável conforme a fonte SIGTAP.")
    private Integer maximumAge;
    @Schema(description = "Código do grupo SIGTAP.")
    private String groupCode;
    @Schema(description = "Nome do grupo SIGTAP.")
    private String groupName;
    @Schema(description = "Código do subgrupo SIGTAP.")
    private String subGroupCode;
    @Schema(description = "Nome do subgrupo SIGTAP.")
    private String subGroupName;
    @Schema(description = "Código da forma de organização SIGTAP.")
    private String organizationCode;
    @Schema(description = "Nome da forma de organização SIGTAP.")
    private String organizationName;
}
