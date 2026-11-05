package com.project.healthsystem.controller.dto.basic_requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCommonStatusDTO {
    @jakarta.validation.constraints.Size(max = 1000, message = "A justificativa não pode ultrapassar 1000 caracteres!")
    private String statusChangeReason;


    @NotNull(message = "O status é obrigatório!")
    @Size(max = 30, message = "O status não pode ultrapassar 30 caracteres!")
    private String status;

    @NotNull
    private Long[] appointmentsId;
}
