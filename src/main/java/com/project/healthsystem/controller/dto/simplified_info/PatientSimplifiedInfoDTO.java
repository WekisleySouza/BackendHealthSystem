package com.project.healthsystem.controller.dto.simplified_info;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PatientSimplifiedInfoDTO", description = "Dados retornados pela API. PatientSimplifiedInfoDTO.")
public record PatientSimplifiedInfoDTO(
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    long id,
    @Schema(description = "Nome de exibição do registro.")
    String name,
    @Schema(description = "CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.")
    String cpf,
    @Schema(description = "Nome da mãe do paciente.")
    String motherName
){}
