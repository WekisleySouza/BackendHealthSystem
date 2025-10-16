package com.project.healthsystem.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private long id;
    @NotNull(message = "O status é obrigatório!")
    private Long statusId;
    private Long professionalId;
    @NotNull(message = "O funcionário responsável é obrigatório!")
    private Long employeeId;

    @NotNull(message = "A pessoa é obrigatória!")
    private Long personId;

    @NotNull(message = "O tipo de serviço é obrigatório!")
    private Long serviceTypeId;

    private String notes;

    private LocalDateTime scheduledAt;

    private LocalDateTime createdAt;

    private String priority;

}
