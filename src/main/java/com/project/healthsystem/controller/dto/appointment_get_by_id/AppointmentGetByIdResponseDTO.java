package com.project.healthsystem.controller.dto.appointment_get_by_id;

import java.time.LocalDateTime;

public record AppointmentGetByIdResponseDTO (
    long id,

    ProfessionalInfoResponseDTO professional,
    EmployeeInfoResponseDTO employee,
    PatientInfoResponseDTO patient,
    ServiceTypeInfoResponseDTO serviceType,

    String status,
    String notes,
    String priority,

    LocalDateTime scheduledAt,
    LocalDateTime createdAt
){}
