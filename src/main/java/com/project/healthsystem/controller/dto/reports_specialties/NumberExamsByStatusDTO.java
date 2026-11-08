package com.project.healthsystem.controller.dto.reports_specialties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "NumberExamsByStatusDTO", description = "Dados retornados pela API. NumberExamsByStatusDTO.")
public record NumberExamsByStatusDTO(
    @Schema(description = "ID do serviço/procedimento cadastrado no Vitalya. Consulte GET /service-types; não é o ID do catálogo SIGTAP.")
    Long serviceTypeId,
    @Schema(description = "Nome do serviço/procedimento cadastrado.")
    String serviceTypeName,
    @Schema(description = "Quantidade de atendimentos agendados.")
    Long scheduledCount,
    @Schema(description = "Quantidade de atendimentos completos.")
    Long completedCount,
    @Schema(description = "Quantidade de atendimentos cancelados.")
    Long canceledCount,
    @Schema(description = "Quantidade de atendimentos com paciente faltante.")
    Long noShowCount,
    @Schema(description = "Quantidade de atendimentos atrasados.")
    Long overdueCount,
    @Schema(description = "Quantidade de atendimentos sem agendamento.")
    Long pendingSchedulingCount,
    @Schema(description = "Quantidade total de atendimentos do serviço.")
    Long totalCount
) {
}