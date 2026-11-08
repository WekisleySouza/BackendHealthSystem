package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.http.HttpStatus;

import java.util.List;

@Schema(name = "ErrorResponseDTO", description = "Dados retornados pela API. ErrorResponseDTO.")
public record ErrorResponseDTO(
    @Schema(description = "Código HTTP do erro, como 400, 401, 403, 404, 409, 413, 422 ou 500.")
    int status,
    @Schema(description = "Mensagem legível que explica o resultado ou erro.")
    String message,
    @Schema(description = "Erros específicos por campo; lista vazia quando não houver detalhamento.")
    List<FieldErrorResponseDTO> errors
) {

    public static ErrorResponseDTO defaultResponse(String message){
        return new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }

    public static ErrorResponseDTO conflict(String message){
        return new ErrorResponseDTO(HttpStatus.CONFLICT.value(), message, List.of());
    }

    public static ErrorResponseDTO notFound(String message){
        return new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), message, List.of());
    }

    public static ErrorResponseDTO invalidData(String message){
        return new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }
}
