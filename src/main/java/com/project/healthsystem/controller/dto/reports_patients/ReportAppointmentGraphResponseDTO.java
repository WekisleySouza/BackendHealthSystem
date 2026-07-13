package com.project.healthsystem.controller.dto.reports_patients;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportAppointmentGraphResponseDTO {
    private Long total;
    private Long totalPendingScheduling;
    private Long totalCompleted;
    private Long totalCanceled;
    private Long totalExcusedAbsense;
    private Long totalUnjustifiededAbsense;
    private Long totalNoShow;
    private Long totalScheduled;
    private Long totalPreScheduled;
    private Long totalOverdue;
}
