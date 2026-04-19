package com.project.healthsystem.controller.dto.basic_requests;

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
public class AppointmentRequestDTO {

    private Long responsibleProfessionalId;
    private Long requestingProfessionalId;
    @NotNull(message = "O funcionário responsável é obrigatório!")
    private Long employeeId;

    @NotNull(message = "O paciente é obrigatório!")
    private Long patientId;

    @NotNull(message = "O status é obrigatório!")
    @Size(max = 30, message = "O status não pode ultrapassar 30 caracteres!")
    private String status;

    @NotNull(message = "O tipo de serviço é obrigatório!")
    private Long serviceTypeId;

    @Size(max = 1000, message = "As observações não podem ultrapassar 1000 caracteres!")
    private String notes;
    private boolean isReturn;

    private LocalDateTime scheduledAt;
    private LocalDateTime schedulingForecast;

    @PastOrPresent(message = "A data de criação não pode estar no futuro!")
    private LocalDateTime createdAt;

    @NotNull(message = "A prioridade é obrigatória!")
    private String priority;
}
