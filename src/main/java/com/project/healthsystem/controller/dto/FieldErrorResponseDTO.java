package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FieldErrorResponseDTO", description = "Dados retornados pela API. FieldErrorResponseDTO.")
public record FieldErrorResponseDTO(
    @Schema(description = "Nome do campo que falhou na validação.")
    String fieldName,
    @Schema(description = "Mensagem legível que explica o resultado ou erro.")
    String message
){

}
