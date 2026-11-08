package com.project.healthsystem.controller.dto.appointment_get_by_id;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "AppointmentGetByIdResponseDTO", description = "Dados retornados pela API. AppointmentGetByIdResponseDTO.")
public record AppointmentGetByIdResponseDTO (
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    long id,
    @Schema(description = "Dados resumidos do profissional responsável.")
    ProfessionalInfoResponseDTO responsibleProfessional,
    @Schema(description = "Dados resumidos do profissional solicitante.")
    ProfessionalInfoResponseDTO requestingProfessional,
    @Schema(description = "Funcionário responsável pelo atendimento.")
    EmployeeInfoResponseDTO employee,
    @Schema(description = "Dados resumidos do paciente.")
    PatientInfoResponseDTO patient,
    @Schema(description = "Classificação do serviço: Exame ou Especialidade.")
    ServiceTypeInfoResponseDTO serviceType,
    @Schema(description = "Grupo de categorias do serviço.")
    CategoryGroupInfoResponseDTO categoryGroup,
    @Schema(description = "Instituição do vínculo legado do atendimento.")
    InstituitionInfoResponseDTO instituitionInfoResponseDTO,
    @Schema(description = "Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", allowableValues = {"Pré agendado", "Completo", "Cancelado", "Falta justificada", "Falta não justificada", "Paciente faltou", "Agendado", "Sem agendamento", "Atrasado"}, example = "Pré agendado")
    String status,
    @Schema(description = "Nome ou rótulo do convênio retornado pelo atendimento.")
    String agreements,
    @Schema(description = "Observações do atendimento; até 1000 caracteres.")
    String notes,
    @Schema(description = "Prioridade do atendimento. Valores: Eletivo, Prioritário, Urgente.", allowableValues = {"Eletivo", "Prioritário", "Urgente"}, example = "Eletivo")
    String priority,
    @Schema(description = "Indica se o atendimento é um retorno. false quando omitido em DTO com boolean primitivo.", example = "false")
    boolean isReturn,
    @Schema(description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime scheduledAt,
    @Schema(description = "Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime createdAt,
    @Schema(description = "Previsão de data e hora do agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime schedulingForecast,
    @Schema(description = "Instituição solicitante do atendimento.")
    InstituitionInfoResponseDTO requestingInstituition,
    @Schema(description = "Instituição reguladora do atendimento.")
    InstituitionInfoResponseDTO regulatingInstituition,
    @Schema(description = "Instituição autora do registro.")
    InstituitionInfoResponseDTO authorInstituition,
    @Schema(description = "Instituição executante do atendimento.")
    InstituitionInfoResponseDTO executingInstituition,
    @Schema(description = "Modalidade assistencial: Ambulatorial para atendimento ambulatorial; Hospitalar para atendimento hospitalar. Valores: Ambulatorial, Hospitalar.", allowableValues = {"Ambulatorial", "Hospitalar"}, example = "Ambulatorial")
    String careModality,
    @Schema(description = "Caráter da solicitação: Eletivo ou Urgente. É independente da prioridade do atendimento. Valores: Eletivo, Urgente.", allowableValues = {"Eletivo", "Urgente"}, example = "Eletivo")
    String requestCharacter,
    @Schema(description = "Catálogo do motivo clínico: CID-10 ou CIAP-2. Deve ser informado junto com clinicalReasonCode. Valores: CID-10, CIAP-2.", allowableValues = {"CID-10", "CIAP-2"}, example = "CID-10")
    String clinicalReasonSystem,
    @Schema(description = "Código textual do motivo clínico, entre 1 e 20 caracteres. Selecione exatamente o code retornado em /datasus/cid10 ou /datasus/ciap2. Código novo deve existir no catálogo selecionado.", minLength = 1, maxLength = 20)
    String clinicalReasonCode,
    @Schema(description = "Data efetiva da solicitação assistencial; não é a data de criação do cadastro. Não pode estar no futuro. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    LocalDateTime requestedAt,
    @Schema(description = "Código CBO da ocupação executante. No atendimento, aceita exatamente seis dígitos, como texto; difere do catálogo CNES que pode conter CBO alfanumérico.", example = "225125", pattern = "^[0-9]{6}$")
    String executingCbo
){}
