package com.project.healthsystem.controller.dto;

import java.time.LocalDateTime;

public record AppointmentReportCountByServiceTypeByProfessionalResponse(
    Long professionalId,
    String professionalName,
    String serviceTypeName,
    LocalDateTime scheduledAt,
    Long total
) {
}
