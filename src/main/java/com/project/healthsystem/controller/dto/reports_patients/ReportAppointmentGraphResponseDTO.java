package com.project.healthsystem.controller.dto.reports_patients;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportAppointmentGraphResponseDTO {
    private Long total;
    private Long totalPendingScheduling;
    private Long totalScheduled;
    private Long totalCompleted;
    private Long totalCanceled;
    private Long totalNoShow;
    private Long totalOverdue;
}
