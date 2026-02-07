package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Status;

import java.time.LocalDateTime;

public record PatientInfoAppointmentResponseDTO (
    long id,
    String professionalName,
    String employeeName,
    String patientName,
    Status status,
    String serviceTypeName,
    String notes,
    String priorit,
    LocalDateTime scheduledAt,
    LocalDateTime createdAt
){
}
