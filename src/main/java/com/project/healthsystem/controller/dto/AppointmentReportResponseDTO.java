package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.project.healthsystem.model.Agreements;
import com.project.healthsystem.model.Priority;
import com.project.healthsystem.model.ServiceTypes;
import com.project.healthsystem.model.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor

@Schema(name = "AppointmentReportResponseDTO", description = "Dados retornados pela API. AppointmentReportResponseDTO.")
public class AppointmentReportResponseDTO {
    
    @Schema(description = "Nome do paciente.")
        private String patientName;
    
    @Schema(description = "Nome da mãe do paciente.")
        private String motherName;
    
    @Schema(description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
        private LocalDateTime scheduledAt;
    
    @Schema(description = "Nome ou rótulo do convênio retornado pelo atendimento.")
        private String agreements;
    
    @Schema(description = "Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", allowableValues = {"Pré agendado", "Completo", "Cancelado", "Falta justificada", "Falta não justificada", "Paciente faltou", "Agendado", "Sem agendamento", "Atrasado"}, example = "Pré agendado")
        private String status;
    
    @Schema(description = "Nome do profissional responsável.")
        private String responsibleProfessionalName;
    
    @Schema(description = "Nome do profissional solicitante.")
        private String requestingProfessionalName;
    
    @Schema(description = "Nome da instituição do vínculo legado.")
        private String instituitionName;
    
    @Schema(description = "Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente.", allowableValues = {"Eletivo", "Prioritário", "Urgente"}, example = "Eletivo")
        private String priorit;
    
    @Schema(description = "Nome do serviço/procedimento.")
        private String serviceName;
    
    @Schema(description = "Classificação do serviço: Exame ou Especialidade. Valores: Exame, Especialidade.", allowableValues = {"Exame", "Especialidade"}, example = "Exame")
        private String serviceType;

    public AppointmentReportResponseDTO(
            // Este construtor é usado no repository
            String patientName,
            String motherName,
            LocalDateTime scheduledAt,
            Agreements agreements,
            Status status,
            String responsibleProfessionalName,
            String requestingProfessionalName,
            String instituitionName,
            Priority priorit,
            String serviceName,
            ServiceTypes serviceType
    ){
        this.patientName = patientName;
        this.motherName = motherName;
        this.scheduledAt = scheduledAt;
        this.agreements = agreements != null ? agreements.getLabel() : null;
        this.status = status != null ? status.getLabel() : null;
        this.responsibleProfessionalName = responsibleProfessionalName;
        this.requestingProfessionalName = requestingProfessionalName;
        this.instituitionName = instituitionName;
        this.priorit = priorit != null ? priorit.getLabel() : null;
        this.serviceName = serviceName;
        this.serviceType = serviceType != null ? serviceType.getLabel() : null;
    }

}
