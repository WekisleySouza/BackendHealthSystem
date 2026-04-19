package com.project.healthsystem.controller.dto.reports_patients;

import java.time.LocalDateTime;

public record PatientReportReponseDTO(
    String patientName,
    String motherName,
    LocalDateTime scheduledAt,
    String status,
    String responsibleProfessionalName,
    String requestingProfessionalName,
    String priorit,
    String serviceName,
    String serviceType
) {
}
