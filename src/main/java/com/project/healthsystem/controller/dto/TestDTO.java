package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Status;

import java.time.LocalDateTime;

public record TestDTO(
        String patientName,
        String motherName,
        LocalDateTime scheduledAt,
        Status status,
        String professionalName
) {
}