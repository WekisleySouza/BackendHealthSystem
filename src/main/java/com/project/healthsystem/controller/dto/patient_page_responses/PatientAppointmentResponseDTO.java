package com.project.healthsystem.controller.dto.patient_page_responses;

import com.project.healthsystem.model.Agreements;
import com.project.healthsystem.model.Priority;
import com.project.healthsystem.model.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PatientAppointmentResponseDTO {
    private String notes;
    private String priorit;
    private String status;
    private String agreements;
    private boolean isReturn;
    private LocalDateTime schedulingForecast;
    private LocalDateTime scheduledAt;

    // Intituition
    private String name;
    private String cep;
    private String cityName;
    private String address;
    private String phone;
    private String linkLogo;

    // Service Type
    private String serviceTypeName;
    private BigDecimal value;

    // Requesting Professional
    private String requestingProfessionalName;

    // Responsible Professional
    private String responsibleProfessionalName;

    public PatientAppointmentResponseDTO(
            String notes,
            Priority priority,
            Status status,
            Agreements agreement,
            boolean isReturn,
            LocalDateTime schedulingForecast,
            LocalDateTime scheduledAt,

            String name,
            String cep,
            String cityName,
            String address,
            String phone,
            String linkLogo,

            String serviceTypeName,
            BigDecimal value,

            String requestingProfessionalName,
            String responsibleProfessionalName
    ) {
        this.notes = notes;

        this.priorit = priority != null
                ? priority.getLabel()
                : null;

        this.status = status != null
                ? status.getLabel()
                : null;

        this.agreements = agreement != null
                ? agreement.getLabel()
                : null;

        this.isReturn = isReturn;
        this.schedulingForecast = schedulingForecast;
        this.scheduledAt = scheduledAt;

        this.name = name;
        this.cep = cep;
        this.cityName = cityName;
        this.address = address;
        this.phone = phone;
        this.linkLogo = linkLogo;

        this.serviceTypeName = serviceTypeName;
        this.value = value;

        this.requestingProfessionalName = requestingProfessionalName;
        this.responsibleProfessionalName = responsibleProfessionalName;
    }
}
