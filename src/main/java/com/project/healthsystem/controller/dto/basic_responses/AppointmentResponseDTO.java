package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "AppointmentResponseDTO", description = "Dados retornados pela API. AppointmentResponseDTO.")
public class AppointmentResponseDTO {
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    private long id;

    @Schema(description = "Modalidade assistencial: Ambulatorial para atendimento ambulatorial; Hospitalar para atendimento hospitalar. Valores: Ambulatorial, Hospitalar.", allowableValues = {"Ambulatorial", "Hospitalar"}, example = "Ambulatorial")
    private String careModality;
    @Schema(description = "Caráter da solicitação: Eletivo ou Urgente. É independente da prioridade do atendimento. Valores: Eletivo, Urgente.", allowableValues = {"Eletivo", "Urgente"}, example = "Eletivo")
    private String requestCharacter;
    @Schema(description = "Catálogo do motivo clínico: CID-10 ou CIAP-2. Deve ser informado junto com clinicalReasonCode. Valores: CID-10, CIAP-2.", allowableValues = {"CID-10", "CIAP-2"}, example = "CID-10")
    private String clinicalReasonSystem;
    @Schema(description = "Código textual do motivo clínico, entre 1 e 20 caracteres. Selecione exatamente o code retornado em /datasus/cid10 ou /datasus/ciap2. Código novo deve existir no catálogo selecionado.", minLength = 1, maxLength = 20)
    private String clinicalReasonCode;
    @Schema(description = "Data efetiva da solicitação assistencial; não é a data de criação do cadastro. Não pode estar no futuro. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    private LocalDateTime requestedAt;
    @Schema(description = "Código CBO da ocupação executante. No atendimento, aceita exatamente seis dígitos, como texto; difere do catálogo CNES que pode conter CBO alfanumérico.", example = "225125", pattern = "^[0-9]{6}$")
    private String executingCbo;

    @Schema(description = "Nome do profissional responsável.")
    private String responsibleProfessionalName;
    @Schema(description = "Nome do profissional solicitante.")
    private String requestingProfessionalName;
    @Schema(description = "Nome do funcionário responsável.")
    private String employeeName;
    @Schema(description = "Nome do paciente.")
    private String patientName;
    @Schema(description = "Nome do serviço/procedimento cadastrado.")
    private String serviceTypeName;
    @Schema(description = "Classificação do serviço: Exame ou Especialidade. Valores: Exame, Especialidade.", allowableValues = {"Exame", "Especialidade"}, example = "Exame")
    private String serviceType;
    @Schema(description = "Nome do grupo de categorias do serviço.")
    private String categoryGroupName;
    @Schema(description = "Nome da instituição do vínculo legado.")
    private String instituitionName;
    @Schema(description = "ID da instituição solicitante no Vitalya: unidade que solicita o atendimento. Consulte GET /instituition.")
    private Long requestingInstituitionId;
    @Schema(description = "Nome da instituição solicitante.")
    private String requestingInstituitionName;
    @Schema(description = "ID da instituição reguladora: unidade que regula a solicitação. Consulte GET /instituition.")
    private Long regulatingInstituitionId;
    @Schema(description = "Nome da instituição reguladora.")
    private String regulatingInstituitionName;
    @Schema(description = "ID da instituição autora: unidade responsável pela autoria do registro. Consulte GET /instituition.")
    private Long authorInstituitionId;
    @Schema(description = "Nome da instituição autora.")
    private String authorInstituitionName;
    @Schema(description = "ID da instituição executante: unidade que realiza o atendimento. Consulte GET /instituition.")
    private Long executingInstituitionId;
    @Schema(description = "Nome da instituição executante.")
    private String executingInstituitionName;
    @Schema(description = "Nome do convênio cadastrado.")
    private String agreementName;

    @Schema(description = "Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", allowableValues = {"Pré agendado", "Completo", "Cancelado", "Falta justificada", "Falta não justificada", "Paciente faltou", "Agendado", "Sem agendamento", "Atrasado"}, example = "Pré agendado")
    private String status;
    @Schema(description = "Observações do atendimento; até 1000 caracteres.")
    private String notes;
    @Schema(description = "Prioridade do atendimento. Valores: Eletivo, Prioritário, Urgente.", allowableValues = {"Eletivo", "Prioritário", "Urgente"}, example = "Eletivo")
    private String priority;
    @Schema(description = "Indica se o atendimento é um retorno. false quando omitido em DTO com boolean primitivo.", name = "return", example = "false")
    private boolean isReturn;

    @Schema(description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    private LocalDateTime scheduledAt;
    @Schema(description = "Previsão de data e hora do agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    private LocalDateTime schedulingForecast;
    @Schema(description = "Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    private LocalDateTime createdAt;
}
