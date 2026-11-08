package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "AppointmentStatusHistoryResponseDTO", description = "Dados retornados pela API. AppointmentStatusHistoryResponseDTO.")
public record AppointmentStatusHistoryResponseDTO(
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    long id,
    @Schema(description = "Status anterior à alteração; pode ser null no primeiro registro. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", allowableValues = {"Pré agendado", "Completo", "Cancelado", "Falta justificada", "Falta não justificada", "Paciente faltou", "Agendado", "Sem agendamento", "Atrasado"}, example = "Pré agendado")
    String previousStatus,
    @Schema(description = "Status após a alteração. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", allowableValues = {"Pré agendado", "Completo", "Cancelado", "Falta justificada", "Falta não justificada", "Paciente faltou", "Agendado", "Sem agendamento", "Atrasado"}, example = "Pré agendado")
    String newStatus,
    @Schema(description = "Data e hora em que a alteração ocorreu, atribuídas pelo backend. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime occurredAt,
    @Schema(description = "Data e hora de gravação do evento no histórico, atribuídas pelo backend. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime recordedAt,
    @Schema(description = "ID da pessoa autora da alteração; null em mudanças automáticas.")
    Long changedById,
    @Schema(description = "Nome do autor da alteração; Sistema para mudanças automáticas.")
    String changedByName,
    @Schema(description = "Justificativa registrada para a alteração de status; pode ser null.")
    String reason
) {}
