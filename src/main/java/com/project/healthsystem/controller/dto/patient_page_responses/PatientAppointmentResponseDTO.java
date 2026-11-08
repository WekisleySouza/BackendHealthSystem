package com.project.healthsystem.controller.dto.patient_page_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import com.project.healthsystem.model.Priority;
import com.project.healthsystem.model.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Schema(name = "PatientAppointmentResponseDTO", description = "Dados retornados pela API. PatientAppointmentResponseDTO.")
public class PatientAppointmentResponseDTO {
    @Schema(description = "Observações do atendimento; até 1000 caracteres.")
    private String notes;
    @Schema(description = "Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente.", allowableValues = {"Eletivo", "Prioritário", "Urgente"}, example = "Eletivo")
    private String priorit;
    @Schema(description = "Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", allowableValues = {"Pré agendado", "Completo", "Cancelado", "Falta justificada", "Falta não justificada", "Paciente faltou", "Agendado", "Sem agendamento", "Atrasado"}, example = "Pré agendado")
    private String status;
    @Schema(description = "Nome ou rótulo do convênio retornado pelo atendimento.")
    private String agreements;
    @Schema(description = "Indica se o atendimento é um retorno. false quando omitido em DTO com boolean primitivo.", name = "return", example = "false")
    private boolean isReturn;
    @Schema(description = "Previsão de data e hora do agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    private LocalDateTime schedulingForecast;
    @Schema(description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    private LocalDateTime scheduledAt;

    // Intituition
    @Schema(description = "Nome da instituição associada ao atendimento.")
    private String name;
    @Schema(description = "Código postal como texto; preserve zeros à esquerda.")
    private String cep;
    @Schema(description = "Nome do município.")
    private String cityName;
    @Schema(description = "Endereço completo informado no cadastro.")
    private String address;
    @Schema(description = "Telefone da instituição.")
    private String phone;
    @Schema(description = "URL do logotipo da instituição.")
    private String linkLogo;

    // Service Type
    @Schema(description = "Nome do serviço/procedimento cadastrado.")
    private String serviceTypeName;
    @Schema(description = "Valor monetário do serviço, como número decimal. No cadastro, deve ser maior que zero e ter até duas casas decimais.", example = "100.00")
    private BigDecimal value;

    // Requesting CnesProfessional
    @Schema(description = "Nome do profissional solicitante.")
    private String requestingProfessionalName;

    // Responsible CnesProfessional
    @Schema(description = "Nome do profissional responsável.")
    private String responsibleProfessionalName;

    public PatientAppointmentResponseDTO(
            String notes,
            Priority priority,
            Status status,
            String agreement,
            boolean isReturn,
            LocalDateTime schedulingForecast,
            LocalDateTime scheduledAt,

            String name,
            String cep,
            String cityName,
            String address,
            String phone,
            String linkLogo,

            String serviceTypeName,
            BigDecimal value,

            String requestingProfessionalName,
            String responsibleProfessionalName
    ) {
        this.notes = notes;

        this.priorit = priority != null
                ? priority.getLabel()
                : null;

        this.status = status != null
                ? status.getLabel()
                : null;

        this.agreements = agreement;

        this.isReturn = isReturn;
        this.schedulingForecast = schedulingForecast;
        this.scheduledAt = scheduledAt;

        this.name = name;
        this.cep = cep;
        this.cityName = cityName;
        this.address = address;
        this.phone = phone;
        this.linkLogo = linkLogo;

        this.serviceTypeName = serviceTypeName;
        this.value = value;

        this.requestingProfessionalName = requestingProfessionalName;
        this.responsibleProfessionalName = responsibleProfessionalName;
    }
}
