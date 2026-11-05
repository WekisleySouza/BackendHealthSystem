package com.project.healthsystem.controller.dto.basic_responses;

import java.time.LocalDateTime;

public record AppointmentStatusHistoryResponseDTO(
    long id,
    String previousStatus,
    String newStatus,
    LocalDateTime occurredAt,
    LocalDateTime recordedAt,
    Long changedById,
    String changedByName,
    String reason
) {}
