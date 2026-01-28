package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Status;

public record AppointmentStatusCountResponseDTO(
        Status status,
        Long total
) {
}
