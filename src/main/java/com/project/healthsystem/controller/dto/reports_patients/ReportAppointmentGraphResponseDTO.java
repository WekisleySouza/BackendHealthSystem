package com.project.healthsystem.controller.dto.reports_patients;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "ReportAppointmentGraphResponseDTO", description = "Dados retornados pela API. ReportAppointmentGraphResponseDTO.")
public class ReportAppointmentGraphResponseDTO {
    @Schema(description = "Quantidade total de registros no agrupamento.")
    private Long total;
    @Schema(description = "Quantidade de atendimentos sem agendamento.")
    private Long totalPendingScheduling;
    @Schema(description = "Quantidade de atendimentos completos.")
    private Long totalCompleted;
    @Schema(description = "Quantidade de atendimentos cancelados.")
    private Long totalCanceled;
    @Schema(description = "Quantidade de atendimentos com falta justificada.")
    private Long totalExcusedAbsense;
    @Schema(description = "Quantidade de atendimentos com falta não justificada.")
    private Long totalUnjustifiededAbsense;
    @Schema(description = "Quantidade de atendimentos com paciente faltante.")
    private Long totalNoShow;
    @Schema(description = "Quantidade de atendimentos agendados.")
    private Long totalScheduled;
    @Schema(description = "Quantidade de atendimentos pré-agendados.")
    private Long totalPreScheduled;
    @Schema(description = "Quantidade de atendimentos atrasados.")
    private Long totalOverdue;
}
