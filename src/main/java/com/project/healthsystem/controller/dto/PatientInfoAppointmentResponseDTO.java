package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "PatientInfoAppointmentResponseDTO", description = "Dados retornados pela API. PatientInfoAppointmentResponseDTO.")
public record PatientInfoAppointmentResponseDTO (
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    long id,
    @Schema(description = "Nome do profissional responsável.")
    String responsibleProfessionalName,
    @Schema(description = "Nome do profissional solicitante.")
    String requestingProfessionalName,
    @Schema(description = "Nome do funcionário responsável.")
    String employeeName,
    @Schema(description = "Nome do paciente.")
    String patientName,
    @Schema(description = "Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", allowableValues = {"Pré agendado", "Completo", "Cancelado", "Falta justificada", "Falta não justificada", "Paciente faltou", "Agendado", "Sem agendamento", "Atrasado"}, example = "Pré agendado")
    String status,
    @Schema(description = "Nome do serviço/procedimento cadastrado.")
    String serviceTypeName,
    @Schema(description = "Observações do atendimento; até 1000 caracteres.")
    String notes,
    @Schema(description = "Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente.", allowableValues = {"Eletivo", "Prioritário", "Urgente"}, example = "Eletivo")
    String priorit,
    @Schema(description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime scheduledAt,
    @Schema(description = "Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime createdAt
){
}
