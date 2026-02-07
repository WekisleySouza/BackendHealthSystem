package com.project.healthsystem.controller.dto;

import java.time.LocalDateTime;

public record ReportAppointmentCountByServiceTypeByProfessionalResponse(
    Long professionalId,
    String professionalName,
    String serviceTypeName,
    LocalDateTime scheduledAt,
    Long total
) {
}
