package com.project.healthsystem.controller.dto.reports_patients;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.web.bind.annotation.BindParam;

import java.time.LocalDateTime;

@Schema(name = "PatientReportFilterDTO", description = "Dados enviados pelo frontend. PatientReportFilterDTO.")
public record PatientReportFilterDTO(
    @Schema(description = "Nome do paciente.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    @BindParam("patient-name") String patientName,
    @Schema(description = "Nome da mãe usado para filtrar pacientes.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    @BindParam("mother-name") String patientMotherName,
    @Schema(description = "Nome do profissional responsável.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    @BindParam("responsible-professional-name") String responsibleProfessionalName,
    @Schema(description = "Nome do profissional solicitante.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    @BindParam("requesting-professional-name") String requestingProfessionalName,
    @Schema(description = "Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, allowableValues = {"Pré agendado", "Completo", "Cancelado", "Falta justificada", "Falta não justificada", "Paciente faltou", "Agendado", "Sem agendamento", "Atrasado"}, example = "Pré agendado", nullable = true)
    @BindParam("status") String status,
    @Schema(description = "Nome do convênio usado no relatório/filtro.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    @BindParam("agreement") String agreement,
    @Schema(description = "Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, allowableValues = {"Eletivo", "Prioritário", "Urgente"}, example = "Eletivo", nullable = true)
    @BindParam("priorit") String priorit,
    @Schema(description = "Tipo/classificação do registro. Valores: Exame, Especialidade.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, allowableValues = {"Exame", "Especialidade"}, example = "Exame", nullable = true)
    @BindParam("type") String type,
    @Schema(description = "Nome do serviço/procedimento.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    @BindParam("service-name") String serviceName,
    @Schema(description = "Nome da instituição do vínculo legado.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    @BindParam("instituition-name") String instituitionName,
    @Schema(description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    @BindParam("scheduled-at") LocalDateTime scheduledAt,
    @Schema(description = "Data e hora inicial do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    @BindParam("scheduled-at-start") LocalDateTime scheduledAtStart,
    @Schema(description = "Data e hora final do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    @BindParam("scheduled-at-end") LocalDateTime scheduledAtEnd,
    @Schema(description = "Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    @BindParam("created-at") LocalDateTime createdAt,
    @Schema(description = "Data e hora inicial do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    @BindParam("created-at-start") LocalDateTime createdAtStart,
    @Schema(description = "Data e hora final do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    @BindParam("created-at-end") LocalDateTime createdAtEnd
) {
}
