package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.SurgeryDTO;
import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "tb_surgery")
@Data
@NoArgsConstructor
public class Surgery extends IDAbstraction {

    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "person_name")
    private String personName;
    @Column(name = "surgery_risk")
    private String surgeryRisk;
    @Column(name = "location")
    private String location;
    @Column(name = "conclusion")
    private String conclusion;
    @Column(name="sus_easy")
    private String susEasy;
    @Column(name = "sesap")
    private String sesap;
    @Column(name = "procedure_date")
    private LocalDate procedureDate;
    @Column(name = "anesthesic_risk")
    private String anesthesicRisk;
    @Column(name = "observation")
    private String observation;

    @ManyToOne
    private SurgeryType surgeryType;

    public Surgery(
                LocalDateTime dateTime,
                String personName,
                String surgeryRisk,
                String location,
                String conclusion,
                String susEasy,
                String sesap,
                LocalDate procedureDate,
                String anesthesicRisk,
                String observation
        ) {
            this.dateTime = dateTime;
            this.personName = personName;
            this.surgeryRisk = surgeryRisk;
            this.location = location;
            this.conclusion = conclusion;
            this.susEasy = susEasy;
            this.sesap = sesap;
            this.procedureDate = procedureDate;
            this.anesthesicRisk = anesthesicRisk;
            this.observation = observation;
        }

        public void coppingFromSurgeryDTO(SurgeryDTO surgeryDTO){
            this.dateTime = LocalDateTime.parse(surgeryDTO.getDateTime(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            this.personName = surgeryDTO.getPersonName();
            this.surgeryRisk = surgeryDTO.getSurgeryRisk();
            this.location = surgeryDTO.getLocation();
            this.conclusion = surgeryDTO.getConclusion();
            this.susEasy = surgeryDTO.getSusEasy();
            this.sesap = surgeryDTO.getSesap();
            this.procedureDate = LocalDate.parse(surgeryDTO.getProcedureDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            this.anesthesicRisk = surgeryDTO.getAnesthesicRisk();
            this.observation = surgeryDTO.getObservation();
        }

        public long getSurgeryTypeId(){
            if(this.surgeryType != null){
                return this.surgeryType.getId();
            }
            return -1;
        }
    }
