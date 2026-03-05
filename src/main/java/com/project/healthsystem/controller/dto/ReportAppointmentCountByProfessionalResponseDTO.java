package com.project.healthsystem.controller.dto;


public record ReportAppointmentCountByProfessionalResponseDTO(
    Long professionalId,
    String professionalName,
    Long total
) {
}
