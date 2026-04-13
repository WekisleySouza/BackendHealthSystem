package com.project.healthsystem.controller.dto.basic_requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PreSchedulingRequestDTO {
    @NotNull
    private Long[] appointmentsId;

}
