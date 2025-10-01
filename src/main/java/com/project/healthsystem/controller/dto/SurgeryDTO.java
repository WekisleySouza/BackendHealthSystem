package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Surgery;
import com.project.healthsystem.model.SurgeryType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryDTO {

    private long id;
    private long surgeryTypeId;

    private String dateTime;
    private String personName;
    private String surgeryRisk;
    private String location;
    private String conclusion;
    private String susEasy;
    private String sesap;
    private String procedureDate;
    private String anesthesicRisk;
    private String observation;

    public SurgeryDTO(Surgery surgery){
        this.id = surgery.getId();
        this.surgeryTypeId = surgery.getSurgeryTypeId();

        this.dateTime = surgery.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.personName = surgery.getPersonName();
        this.surgeryRisk = surgery.getSurgeryRisk();
        this.location = surgery.getLocation();
        this.conclusion = surgery.getConclusion();
        this.susEasy = surgery.getSusEasy();
        this.sesap = surgery.getSesap();
        this.procedureDate = surgery.getProcedureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.anesthesicRisk = surgery.getAnesthesicRisk();
        this.observation = surgery.getObservation();
    }

    public Surgery mappingToSurgery(SurgeryType surgeryType){
        Surgery surgery = new Surgery(
            LocalDateTime.parse(this.dateTime, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            this.personName,
            this.surgeryRisk,
            this.location,
            this.conclusion,
            this.susEasy,
            this.sesap,
            LocalDate.parse(this.procedureDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            this.anesthesicRisk,
            this.observation
        );
        surgery.setSurgeryType(surgeryType);
        return surgery;
    }
}
