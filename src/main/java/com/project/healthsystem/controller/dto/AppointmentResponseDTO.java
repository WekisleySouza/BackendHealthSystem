package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentResponseDTO {
    private long id;

    private Long professionalId;
    private Long employeeId;
    private Long patientId;

    private Status status;
    private Long serviceTypeId;
    private String notes;
    private String priority;

    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
}
