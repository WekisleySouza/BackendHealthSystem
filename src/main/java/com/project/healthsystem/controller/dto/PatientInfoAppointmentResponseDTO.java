package com.project.healthsystem.controller.dto;

import java.time.LocalDateTime;

public record PatientInfoAppointmentResponseDTO (
    long id,
    String professionalName,
    String employeeName,
    String patientName,
    String status,
    String serviceTypeName,
    String notes,
    String priorit,
    LocalDateTime scheduledAt,
    LocalDateTime createdAt
){
}
