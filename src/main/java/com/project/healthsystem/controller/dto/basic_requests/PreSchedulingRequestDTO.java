package com.project.healthsystem.controller.dto.basic_requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PreSchedulingRequestDTO {
    @jakarta.validation.constraints.Size(max = 1000, message = "A justificativa não pode ultrapassar 1000 caracteres!")
    private String statusChangeReason;

    @NotNull
    private Long[] appointmentsId;
}
