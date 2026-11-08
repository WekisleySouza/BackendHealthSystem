package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SigtapImportResponseDTO", description = "Dados retornados pela API. SigtapImportResponseDTO.")
public record SigtapImportResponseDTO(
    @Schema(description = "Indica que a importação SIGTAP foi concluída.", example = "false")
    boolean imported,
    @Schema(description = "Competência da base, no formato AAAAMM.", example = "202607", pattern = "^[0-9]{6}$")
    String competence,
    @Schema(description = "Nome do arquivo de origem importado.")
    String fileName,
    @Schema(description = "Quantidade de grupos importados.")
    long groups,
    @Schema(description = "Quantidade de subgrupos importados.")
    long subGroups,
    @Schema(description = "Quantidade de formas de organização importadas.")
    long organizations,
    @Schema(description = "Quantidade de procedimentos importados.")
    long procedures
) {}
