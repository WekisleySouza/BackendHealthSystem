package com.project.healthsystem.controller.dto.reports_patients;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "PatientReportReponseDTO", description = "Dados retornados pela API. PatientReportReponseDTO.")
public record PatientReportReponseDTO(
    @Schema(description = "Nome do paciente.")
    String patientName,
    @Schema(description = "Nome da mãe do paciente.")
    String motherName,
    @Schema(description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime scheduledAt,
    @Schema(description = "Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime createdAt,
    @Schema(description = "Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", allowableValues = {"Pré agendado", "Completo", "Cancelado", "Falta justificada", "Falta não justificada", "Paciente faltou", "Agendado", "Sem agendamento", "Atrasado"}, example = "Pré agendado")
    String status,
    @Schema(description = "Nome do profissional responsável.")
    String responsibleProfessionalName,
    @Schema(description = "Nome do profissional solicitante.")
    String requestingProfessionalName,
    @Schema(description = "Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente.", allowableValues = {"Eletivo", "Prioritário", "Urgente"}, example = "Eletivo")
    String priorit,
    @Schema(description = "Nome do serviço/procedimento.")
    String serviceName,
    @Schema(description = "Classificação do serviço: Exame ou Especialidade. Valores: Exame, Especialidade.", allowableValues = {"Exame", "Especialidade"}, example = "Exame")
    String serviceType
) {
}
