package com.project.healthsystem.controller.dto.reports_patients;

import com.project.healthsystem.model.Priority;
import com.project.healthsystem.model.ServiceTypes;
import com.project.healthsystem.model.Status;

import java.time.LocalDateTime;

public record PatientReportRowDTO(
    String patientName,
    String motherName,
    LocalDateTime scheduledAt,
    LocalDateTime createdAt,
    Status status,
    String responsibleProfessionalName,
    String requestingProfessionalName,
    Priority priorit,
    String serviceName,
    ServiceTypes serviceType,
    String agreement
) {
}
