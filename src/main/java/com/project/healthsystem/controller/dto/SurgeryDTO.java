package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Surgery;
import com.project.healthsystem.model.SurgeryType;
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
public class SurgeryDTO {

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

    public SurgeryDTO(Surgery surgery){
        this.id = surgery.getId();
        this.surgeryTypeId = surgery.getSurgeryTypeId();

        this.dateTime = surgery.getDateTime();
        this.personName = surgery.getPersonName();
        this.surgeryRisk = surgery.getSurgeryRisk();
        this.location = surgery.getLocation();
        this.conclusion = surgery.getConclusion();
        this.susEasy = surgery.getSusEasy();
        this.sesap = surgery.getSesap();
        this.procedureDate = surgery.getProcedureDate();
        this.anesthesicRisk = surgery.getAnesthesicRisk();
        this.observation = surgery.getObservation();
    }

    public Surgery mappingToSurgery(SurgeryType surgeryType){
        Surgery surgery = new Surgery(
            this.dateTime,
            this.personName,
            this.surgeryRisk,
            this.location,
            this.conclusion,
            this.susEasy,
            this.sesap,
            this.procedureDate,
            this.anesthesicRisk,
            this.observation
        );
        surgery.setSurgeryType(surgeryType);
        return surgery;
    }
}
