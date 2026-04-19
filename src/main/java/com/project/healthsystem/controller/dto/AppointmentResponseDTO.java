package com.project.healthsystem.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentResponseDTO {
    private long id;

    private String responsibleProfessionalName;
    private String requestingProfessionalName;
    private String employeeName;
    private String patientName;
    private String serviceTypeName;
    private String serviceType;
    private String categoryGroupName;

    private String status;
    private String notes;
    private String priority;
    private boolean isReturn;

    private LocalDateTime scheduledAt;
    private LocalDateTime schedulingForecast;
    private LocalDateTime createdAt;
}
