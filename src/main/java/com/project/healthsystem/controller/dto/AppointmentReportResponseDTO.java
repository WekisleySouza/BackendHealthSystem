package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Priority;
import com.project.healthsystem.model.ServiceTypes;
import com.project.healthsystem.model.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AppointmentReportResponseDTO {
        private String patientName;
        private String motherName;
        private LocalDateTime scheduledAt;
        private String status;
        private String responsibleProfessionalName;
        private String requestingProfessionalName;
        private String priorit;
        private String serviceName;
        private String serviceType;

    public AppointmentReportResponseDTO(
            String patientName,
            String motherName,
            LocalDateTime scheduledAt,
            Status status,
            String responsibleProfessionalName,
            String requestingProfessionalName,
            Priority priorit,
            String serviceName,
            ServiceTypes serviceType
    ){
        this.patientName = patientName;
        this.motherName = motherName;
        this.scheduledAt = scheduledAt;
        this.status = status != null ? status.getLabel() : null;
        this.responsibleProfessionalName = responsibleProfessionalName;
        this.requestingProfessionalName = requestingProfessionalName;
        this.priorit = priorit != null ? priorit.getLabel() : null;
        this.serviceName = serviceName;
        this.serviceType = serviceType != null ? serviceType.getLabel() : null;
    }

}
