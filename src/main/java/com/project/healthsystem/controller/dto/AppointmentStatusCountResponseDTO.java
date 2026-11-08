package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.project.healthsystem.model.Status;

@Schema(name = "AppointmentStatusCountResponseDTO", description = "Dados retornados pela API. AppointmentStatusCountResponseDTO.")
public record AppointmentStatusCountResponseDTO(
    @Schema(description = "Situação atual do atendimento. Neste campo enum tipado, o JSON usa os nomes: PRE_SCHEDULED, COMPLETED, CANCELED, EXCUSED_ABSENSE, UNJUSTIFIED_ABSENSE, NO_SHOW, SCHEDULED, PENDING_SCHEDULING, OVERDUE.")
    Status status,
    @Schema(description = "Quantidade total de registros no agrupamento.")
    Long total
) {
}
