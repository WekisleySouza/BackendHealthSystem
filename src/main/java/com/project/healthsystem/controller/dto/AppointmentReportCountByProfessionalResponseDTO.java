package com.project.healthsystem.controller.dto;

import java.time.LocalDateTime;

public record AppointmentReportCountByProfessionalResponseDTO(
    Long professionalId,
    String professionalName,
    LocalDateTime scheduledAt,
    Long total
) {
}
