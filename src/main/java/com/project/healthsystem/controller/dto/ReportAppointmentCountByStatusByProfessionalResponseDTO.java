package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Status;

import java.time.LocalDateTime;

public record ReportAppointmentCountByStatusByProfessionalResponseDTO(
        Long professionalId,
        String professionalName,
        Status status,
        LocalDateTime scheduledAt,
        Long total
) {
}
