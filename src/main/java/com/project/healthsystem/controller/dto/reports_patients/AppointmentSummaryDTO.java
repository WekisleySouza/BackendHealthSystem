package com.project.healthsystem.controller.dto.reports_patients;

import com.project.healthsystem.model.Status;

public record AppointmentSummaryDTO(
        Status status,
        Long total
) {
}
