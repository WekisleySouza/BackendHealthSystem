package com.project.healthsystem.controller.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record ReportAppointmentByPatientResponseDTO(
        Page<AppointmentReportResponseDTO> appointmentReportResponseDTOS,
        List<AppointmentStatusCountResponseDTO> appointmentStatusCountResponseDTOS
) {
}
