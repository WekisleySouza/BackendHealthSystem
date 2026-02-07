package com.project.healthsystem.controller.dto;

import java.time.LocalDateTime;

public record ReportAppointmentCountByProfessionalResponseDTO(
    Long professionalId,
    String professionalName,
    LocalDateTime scheduledAt,
    Long total
) {
}
