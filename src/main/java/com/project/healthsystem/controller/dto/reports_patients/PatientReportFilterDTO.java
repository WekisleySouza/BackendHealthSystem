package com.project.healthsystem.controller.dto.reports_patients;

import org.springframework.web.bind.annotation.BindParam;

import java.time.LocalDateTime;

public record PatientReportFilterDTO(
    @BindParam("patient-name") String patientName,
    @BindParam("mother-name") String patientMotherName,
    @BindParam("responsible-professional-name") String responsibleProfessionalName,
    @BindParam("requesting-professional-name") String requestingProfessionalName,
    @BindParam("status") String status,
    @BindParam("agreement") String agreement,
    @BindParam("priorit") String priorit,
    @BindParam("type") String type,
    @BindParam("service-name") String serviceName,
    @BindParam("instituition-name") String instituitionName,
    @BindParam("scheduled-at") LocalDateTime scheduledAt,
    @BindParam("scheduled-at-start") LocalDateTime scheduledAtStart,
    @BindParam("scheduled-at-end") LocalDateTime scheduledAtEnd,
    @BindParam("created-at") LocalDateTime createdAt,
    @BindParam("created-at-start") LocalDateTime createdAtStart,
    @BindParam("created-at-end") LocalDateTime createdAtEnd
) {
}
