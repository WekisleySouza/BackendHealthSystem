package com.project.healthsystem.controller.dto;

import org.springframework.data.domain.Page;

public record AppointmentReportByProfessionalResponseDTO(
    Page<AppointmentReportCountByProfessionalResponseDTO> appointmentReportCountByProfessionalResponseDTOS,
    Page<AppointmentReportCountByStatusByProfessionalResponseDTO> appointmentReportCountByStatusByProfessionalResponseDTOS,
    Page<AppointmentReportCountByServiceTypeByProfessionalResponse> appointmentReportCountByServiceTypeByProfessionalResponses,
    Long totalProfessionals
) {
}
