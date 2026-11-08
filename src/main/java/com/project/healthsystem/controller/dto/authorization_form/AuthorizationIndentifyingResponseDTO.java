package com.project.healthsystem.controller.dto.authorization_form;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "AuthorizationIndentifyingResponseDTO", description = "Dados retornados pela API. AuthorizationIndentifyingResponseDTO.")
public record AuthorizationIndentifyingResponseDTO(
    @Schema(description = "Origem do cadastro ou encaminhamento.")
    String origin,
    @Schema(description = "Destino do encaminhamento.")
    String destiny,
    @Schema(description = "Data e hora agendadas para o atendimento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime appointmentScheduledDateTime,
    @Schema(description = "Instituição usada na autorização.")
    InstituitionResponseDTO instituitionResponseDTO
) {
}
