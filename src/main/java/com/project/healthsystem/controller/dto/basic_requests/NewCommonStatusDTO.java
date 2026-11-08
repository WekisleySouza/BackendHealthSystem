package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(name = "NewCommonStatusDTO", description = "Dados enviados pelo frontend. NewCommonStatusDTO.")
public class NewCommonStatusDTO {
    @jakarta.validation.constraints.Size(max = 1000, message = "A justificativa não pode ultrapassar 1000 caracteres!")
    @Schema(description = "Justificativa opcional para a mudança de status; até 1000 caracteres. Registrada no histórico quando houver mudança.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 1000, nullable = true)
    private String statusChangeReason;

    @NotNull(message = "O status é obrigatório!")
    @Size(max = 30, message = "O status não pode ultrapassar 30 caracteres!")
    @Schema(description = "Situação atual do atendimento. Pré agendado não é aceito nesta rota; use a rota de pré-agendamento. Valores: Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"Completo", "Cancelado", "Falta justificada", "Falta não justificada", "Paciente faltou", "Agendado", "Sem agendamento", "Atrasado"}, example = "Completo", maxLength = 30)
    private String status;

    @NotNull
    @Schema(description = "Lista dos IDs de atendimentos a atualizar em lote.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long[] appointmentsId;
}
