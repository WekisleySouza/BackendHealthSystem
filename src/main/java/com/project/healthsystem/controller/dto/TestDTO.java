package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.project.healthsystem.model.Status;

import java.time.LocalDateTime;

@Schema(name = "TestDTO", description = "Dados retornados pela API. TestDTO.")
public record TestDTO(
    @Schema(description = "Nome do paciente.")
    String patientName,
    @Schema(description = "Nome da mãe do paciente.")
    String motherName,
    @Schema(description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime scheduledAt,
    @Schema(description = "Situação atual do atendimento. Neste campo enum tipado, o JSON usa os nomes: PRE_SCHEDULED, COMPLETED, CANCELED, EXCUSED_ABSENSE, UNJUSTIFIED_ABSENSE, NO_SHOW, SCHEDULED, PENDING_SCHEDULING, OVERDUE.")
    Status status,
    @Schema(description = "Nome do profissional responsável.")
    String responsibleProfessionalName,
    @Schema(description = "Nome do profissional solicitante.")
    String requestingProfessionalName
) {
}