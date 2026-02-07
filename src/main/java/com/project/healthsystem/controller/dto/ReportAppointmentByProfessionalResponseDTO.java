package com.project.healthsystem.controller.dto;

import org.springframework.data.domain.Page;

public record ReportAppointmentByProfessionalResponseDTO(
    Page<ReportAppointmentCountByProfessionalResponseDTO> appointmentReportCountByProfessionalResponseDTOS,
    Page<ReportAppointmentCountByStatusByProfessionalResponseDTO> appointmentReportCountByStatusByProfessionalResponseDTOS,
    Page<ReportAppointmentCountByServiceTypeByProfessionalResponse> appointmentReportCountByServiceTypeByProfessionalResponses,
    Long totalProfessionals
) {
}
