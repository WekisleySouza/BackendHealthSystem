package com.project.healthsystem.controller.dto.reports_specialties;

public record NumberSpecialtiesByStatusDTO(
        Long serviceTypeId,
        String serviceTypeName,
        Long scheduledCount,
        Long completedCount,
        Long canceledCount,
        Long noShowCount,
        Long overdueCount,
        Long pendingSchedulingCount,
        Long totalCount
) {
}
