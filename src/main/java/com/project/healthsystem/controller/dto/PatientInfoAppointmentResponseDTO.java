package com.project.healthsystem.controller.dto;

import java.time.LocalDateTime;

public record PatientInfoAppointmentResponseDTO (
    long id,
    String responsibleProfessionalName,
    String requestingProfessionalName,
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
