package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.ConsultationDTO;
import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "consultation")
@Data
@NoArgsConstructor
public class Consultation extends IDAbstraction {

    @Column(name = "description")
    private String description;
    @Column(name="date_hour")
    private LocalDateTime dateHour;
    @Column(name="is_exam")
    private boolean isExam;
    @Column(name="priorit")
    private String priorit;

    @ManyToOne
    private Speciality specialty;
    @ManyToOne
    private Professional professional;
    @ManyToOne
    private Person person;
    @ManyToOne
    private Status status;

    public Consultation(String description, LocalDateTime dateHour, boolean isExam, String priorit) {
        this.description = description;
        this.dateHour = dateHour;
        this.isExam = isExam;
        this.priorit = priorit;
    }

    public void coppingFromConsultationDTO(ConsultationDTO consultationDTO){
        this.description = consultationDTO.getDescription();
        this.dateHour = LocalDateTime.parse(consultationDTO.getDateHour() , DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        this.isExam = consultationDTO.isExam();
        this.priorit = consultationDTO.getPriorit();
    }

    public long getStatusId(){
        if(this.status != null){
            return status.getId();
        }
        return -1;
    }

    public long getProfessionalId(){
        if(this.professional != null){
            return professional.getId();
        }
        return -1;
    }

    public long getPersonId(){
        if(this.person != null){
            return person.getId();
        }
        return -1;
    }

    public long getSpecialtyId(){
        if(this.specialty != null){
            return specialty.getId();
        }
        return -1;
    }
}
