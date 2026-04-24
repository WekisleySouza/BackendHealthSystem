package com.project.healthsystem.controller.dto.appointment_get_by_id;

import java.time.LocalDateTime;

public record AppointmentGetByIdResponseDTO (
    long id,

    ProfessionalInfoResponseDTO responsibleProfessional,
    ProfessionalInfoResponseDTO requestingProfessional,
    EmployeeInfoResponseDTO employee,
    PatientInfoResponseDTO patient,
    ServiceTypeInfoResponseDTO serviceType,
    CategoryGroupInfoResponseDTO categoryGroup,

    String status,
    String agreements,
    String notes,
    String priority,
    boolean isReturn,

    LocalDateTime scheduledAt,
    LocalDateTime createdAt,
    LocalDateTime schedulingForecast
){}
