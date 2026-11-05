package com.project.healthsystem.controller.dto.basic_responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentResponseDTO {
    private long id;

    private String careModality;
    private String requestCharacter;
    private String clinicalReasonSystem;
    private String clinicalReasonCode;
    private LocalDateTime requestedAt;
    private String executingCbo;


    private String responsibleProfessionalName;
    private String requestingProfessionalName;
    private String employeeName;
    private String patientName;
    private String serviceTypeName;
    private String serviceType;
    private String categoryGroupName;
    private String instituitionName;
    private Long requestingInstituitionId;
    private String requestingInstituitionName;
    private Long regulatingInstituitionId;
    private String regulatingInstituitionName;
    private Long authorInstituitionId;
    private String authorInstituitionName;
    private Long executingInstituitionId;
    private String executingInstituitionName;
    private String agreementName;

    private String status;
    private String notes;
    private String priority;
    private boolean isReturn;

    private LocalDateTime scheduledAt;
    private LocalDateTime schedulingForecast;
    private LocalDateTime createdAt;
}
