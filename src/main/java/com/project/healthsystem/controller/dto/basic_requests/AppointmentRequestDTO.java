package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Schema(name = "AppointmentRequestDTO", description = "Dados enviados pelo frontend. AppointmentRequestDTO.")
public class AppointmentRequestDTO {
    @jakarta.validation.constraints.Size(max = 1000, message = "A justificativa não pode ultrapassar 1000 caracteres!")
    
    @Schema(description = "Justificativa opcional para a mudança de status; até 1000 caracteres. Registrada no histórico quando houver mudança.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 1000, nullable = true)
    private String statusChangeReason;

    @Schema(description = "ID do profissional responsável no Vitalya. Consulte GET /professionals. null ou -1 não atribui um novo vínculo.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Long responsibleProfessionalId;
    
    @Schema(description = "ID do profissional solicitante no Vitalya. Consulte GET /professionals. Necessário no cadastro e na atualização do atendimento.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long requestingProfessionalId;
    @NotNull(message = "O funcionário responsável é obrigatório!")
    
    @Schema(description = "ID do funcionário responsável pelo atendimento. Consulte GET /employees.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long employeeId;
    
    @Schema(description = "ID da instituição do vínculo legado. Consulte GET /instituition. Mantido por compatibilidade; não preenche automaticamente os quatro papéis.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Long instituitionId;
    
    @Schema(description = "ID da instituição solicitante no Vitalya: unidade que solicita o atendimento. Consulte GET /instituition. Na atualização, null/omissão preserva o vínculo atual; informe ID positivo.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Long requestingInstituitionId;
    
    @Schema(description = "ID da instituição reguladora: unidade que regula a solicitação. Consulte GET /instituition. Na atualização, null/omissão preserva o vínculo atual; informe ID positivo.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Long regulatingInstituitionId;
    
    @Schema(description = "ID da instituição autora: unidade responsável pela autoria do registro. Consulte GET /instituition. Na atualização, null/omissão preserva o vínculo atual; informe ID positivo.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Long authorInstituitionId;
    
    @Schema(description = "ID da instituição executante: unidade que realiza o atendimento. Consulte GET /instituition. Na atualização, null/omissão preserva o vínculo atual; informe ID positivo.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Long executingInstituitionId;
    
    @Schema(description = "ID do convênio cadastrado. Consulte GET /agreements. null ou -1 não atribui um novo convênio.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Long agreementId;

    @Schema(description = "Modalidade assistencial: Ambulatorial para atendimento ambulatorial; Hospitalar para atendimento hospitalar. Na atualização, null/omissão preserva o valor. Valores: Ambulatorial, Hospitalar.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, allowableValues = {"Ambulatorial", "Hospitalar"}, example = "Ambulatorial", nullable = true)
    private String careModality;
    
    @Schema(description = "Caráter da solicitação: Eletivo ou Urgente. É independente da prioridade do atendimento. Na atualização, null/omissão preserva o valor. Valores: Eletivo, Urgente.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, allowableValues = {"Eletivo", "Urgente"}, example = "Eletivo", nullable = true)
    private String requestCharacter;
    
    @Schema(description = "Catálogo do motivo clínico: CID-10 ou CIAP-2. Deve ser informado junto com clinicalReasonCode. Valores: CID-10, CIAP-2.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, allowableValues = {"CID-10", "CIAP-2"}, example = "CID-10", nullable = true)
    private String clinicalReasonSystem;
    
    @Schema(description = "Código textual do motivo clínico, entre 1 e 20 caracteres. Selecione exatamente o code retornado em /datasus/cid10 ou /datasus/ciap2. Código novo deve existir no catálogo selecionado.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minLength = 1, maxLength = 20, nullable = true)
    private String clinicalReasonCode;
    
    @Schema(description = "Data efetiva da solicitação assistencial; não é a data de criação do cadastro. Não pode estar no futuro. Na atualização, null/omissão preserva o valor. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    private LocalDateTime requestedAt;
    
    @Schema(description = "Código CBO da ocupação executante. No atendimento, aceita exatamente seis dígitos, como texto; difere do catálogo CNES que pode conter CBO alfanumérico.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "225125", pattern = "^[0-9]{6}$", nullable = true)
    private String executingCbo;

    @NotNull(message = "O paciente é obrigatório!")
    
    @Schema(description = "ID do paciente no Vitalya. Consulte GET /patients.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long patientId;

    @NotNull(message = "O status é obrigatório!")
    @Size(max = 30, message = "O status não pode ultrapassar 30 caracteres!")
    
    @Schema(description = "Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"Pré agendado", "Completo", "Cancelado", "Falta justificada", "Falta não justificada", "Paciente faltou", "Agendado", "Sem agendamento", "Atrasado"}, example = "Pré agendado", maxLength = 30)
    private String status;

    @NotNull(message = "O tipo de serviço é obrigatório!")
    
    @Schema(description = "ID do serviço/procedimento cadastrado no Vitalya. Consulte GET /service-types; não é o ID do catálogo SIGTAP.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long serviceTypeId;

    @Size(max = 1000, message = "As observações não podem ultrapassar 1000 caracteres!")
    
    @Schema(description = "Observações do atendimento; até 1000 caracteres.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 1000, nullable = true)
    private String notes;
    
    @Schema(description = "Indica se o atendimento é um retorno. false quando omitido em DTO com boolean primitivo.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, name = "return", example = "false")
    private boolean isReturn;

    @Schema(description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    private LocalDateTime scheduledAt;
    
    @Schema(description = "Previsão de data e hora do agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    private LocalDateTime schedulingForecast;

    @PastOrPresent(message = "A data de criação não pode estar no futuro!")
    
    @Schema(description = "Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    private LocalDateTime createdAt;

    @NotNull(message = "A prioridade é obrigatória!")
    
    @Schema(description = "Prioridade do atendimento. Valores: Eletivo, Prioritário, Urgente.", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"Eletivo", "Prioritário", "Urgente"}, example = "Eletivo")
    private String priority;
}
