package com.project.healthsystem.controller.dto.reports_patients;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record PatientReportStatisticsDTO(
    long totalAppointments,
    long uniquePatients,
    long involvedProfessionals,
    Map<String, Long> byStatus,
    Map<String, Long> byPriority,
    Map<String, Long> byServiceType
) {
    public PatientReportStatisticsDTO {
        byStatus = Collections.unmodifiableMap(new LinkedHashMap<>(byStatus));
        byPriority = Collections.unmodifiableMap(new LinkedHashMap<>(byPriority));
        byServiceType = Collections.unmodifiableMap(new LinkedHashMap<>(byServiceType));
    }
}