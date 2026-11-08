package com.project.healthsystem.controller.dto.reports_patients;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Schema(name = "PatientReportStatisticsDTO", description = "Dados retornados pela API. PatientReportStatisticsDTO.")
public record PatientReportStatisticsDTO(
    @Schema(description = "Quantidade de atendimentos incluídos no relatório.")
    long totalAppointments,
    @Schema(description = "Quantidade de pacientes distintos no relatório.")
    long uniquePatients,
    @Schema(description = "Quantidade de profissionais distintos envolvidos no relatório.")
    long involvedProfessionals,
    @Schema(description = "Contagens agrupadas por status.")
    Map<String, Long> byStatus,
    @Schema(description = "Contagens agrupadas por prioridade.")
    Map<String, Long> byPriority,
    @Schema(description = "Contagens agrupadas por tipo de serviço.")
    Map<String, Long> byServiceType
) {
    public PatientReportStatisticsDTO {
        byStatus = Collections.unmodifiableMap(new LinkedHashMap<>(byStatus));
        byPriority = Collections.unmodifiableMap(new LinkedHashMap<>(byPriority));
        byServiceType = Collections.unmodifiableMap(new LinkedHashMap<>(byServiceType));
    }
}