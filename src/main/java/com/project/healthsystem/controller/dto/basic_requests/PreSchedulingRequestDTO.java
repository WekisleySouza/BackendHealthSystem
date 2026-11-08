package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "PreSchedulingRequestDTO", description = "Dados enviados pelo frontend. PreSchedulingRequestDTO.")
public class PreSchedulingRequestDTO {
    @jakarta.validation.constraints.Size(max = 1000, message = "A justificativa não pode ultrapassar 1000 caracteres!")
    @Schema(description = "Justificativa opcional para a mudança de status; até 1000 caracteres. Registrada no histórico quando houver mudança.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 1000, nullable = true)
    private String statusChangeReason;

    @NotNull
    @Schema(description = "Lista dos IDs de atendimentos a atualizar em lote.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long[] appointmentsId;
}
