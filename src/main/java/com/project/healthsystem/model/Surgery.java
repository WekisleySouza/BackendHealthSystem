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

    public long getSurgeryTypeId(){
        if(this.surgeryType != null){
            return this.surgeryType.getId();
        }
        return -1;
    }
}
