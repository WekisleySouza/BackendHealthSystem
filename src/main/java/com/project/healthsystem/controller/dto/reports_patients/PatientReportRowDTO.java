package com.project.healthsystem.controller.dto.reports_patients;

import io.swagger.v3.oas.annotations.media.Schema;

import com.project.healthsystem.model.Priority;
import com.project.healthsystem.model.ServiceTypes;
import com.project.healthsystem.model.Status;

import java.time.LocalDateTime;

@Schema(name = "PatientReportRowDTO", description = "Dados retornados pela API. PatientReportRowDTO.")
public record PatientReportRowDTO(
    @Schema(description = "Nome do paciente.")
    String patientName,
    @Schema(description = "Nome da mãe do paciente.")
    String motherName,
    @Schema(description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime scheduledAt,
    @Schema(description = "Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime createdAt,
    @Schema(description = "Situação atual do atendimento. Neste campo enum tipado, o JSON usa os nomes: PRE_SCHEDULED, COMPLETED, CANCELED, EXCUSED_ABSENSE, UNJUSTIFIED_ABSENSE, NO_SHOW, SCHEDULED, PENDING_SCHEDULING, OVERDUE.")
    Status status,
    @Schema(description = "Nome do profissional responsável.")
    String responsibleProfessionalName,
    @Schema(description = "Nome do profissional solicitante.")
    String requestingProfessionalName,
    @Schema(description = "Prioridade do atendimento; grafia legada mantida no contrato. Neste campo enum tipado, o JSON usa os nomes: ELECTIVE, PRIORITY, URGENT.")
    Priority priorit,
    @Schema(description = "Nome do serviço/procedimento.")
    String serviceName,
    @Schema(description = "Classificação do serviço: Exame ou Especialidade. Neste campo enum tipado, o JSON usa os nomes: EXAM, SPECIALTY.")
    ServiceTypes serviceType,
    @Schema(description = "Nome do convênio usado no relatório/filtro.")
    String agreement
) {
}
