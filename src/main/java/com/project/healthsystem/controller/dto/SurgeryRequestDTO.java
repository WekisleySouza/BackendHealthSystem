package com.project.healthsystem.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryRequestDTO {

    private long id;
    private long surgeryTypeId;

    private LocalDateTime dateTime;
    private String personName;
    private String surgeryRisk;
    private String location;
    private String conclusion;
    private String susEasy;
    private String sesap;
    private LocalDate procedureDate;
    private String anesthesicRisk;
    private String observation;

}
