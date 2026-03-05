package com.project.healthsystem.controller.dto.reports_professional;

public record NumberAppointmentsByStatusAndProfessionalDTO(
        Long professionalId,
        String professionalName,
        Long scheduledCount,
        Long completedCount,
        Long canceledCount,
        Long noShowCount,
        Long overdueCount,
        Long pendingSchedulingCount,
        Long examCount,
        Long specialtyCount,
        Long totalCount
) {
}
