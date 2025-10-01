package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.*;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationDTO {

    private long id;
    private long statusId;
    private long professionalId;
    private long personId;
    private long specialtyId;

    private String description;
    private String dateHour;
    private boolean isExam;
    private String priorit;

    public ConsultationDTO(Consultation consultation){
        this.id = consultation.getId();
        this.statusId = consultation.getStatusId();
        this.professionalId = consultation.getProfessionalId();
        this.personId = consultation.getPersonId();
        this.specialtyId = consultation.getSpecialtyId();

        this.description = consultation.getDescription();
        this.dateHour = consultation.getDateHour().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        this.isExam = consultation.isExam();
        this.priorit = consultation.getPriorit();
    }

    public Consultation mappingToConsultation(Status status, Speciality specialty){
        Consultation consultation = new Consultation(
                this.description,
                LocalDateTime.parse(this.dateHour, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                this.isExam,
                this.priorit
        );
        consultation.setStatus(status);
        consultation.setSpecialty(specialty);
        return consultation;
    }

    public Consultation mappingToConsultation(Status status, Speciality specialty, Professional professional){
        Consultation consultation = new Consultation(
                this.description,
                LocalDateTime.parse(this.dateHour, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                this.isExam,
                this.priorit
        );
        consultation.setStatus(status);
        consultation.setProfessional(professional);
        consultation.setSpecialty(specialty);
        return consultation;
    }

    public Consultation mappingToConsultation(Status status, Speciality specialty, Person person){
        Consultation consultation = new Consultation(
                this.description,
                LocalDateTime.parse(this.dateHour, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                this.isExam,
                this.priorit
        );
        consultation.setStatus(status);
        consultation.setPerson(person);
        consultation.setSpecialty(specialty);
        return consultation;
    }

    public Consultation mappingToConsultation(
            Status status,
            Speciality specialty,
            Professional professional,
            Person person
    ){
        Consultation consultation = new Consultation(
                this.description,
                LocalDateTime.parse(this.dateHour, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                this.isExam,
                this.priorit
        );
        consultation.setStatus(status);
        consultation.setProfessional(professional);
        consultation.setPerson(person);
        consultation.setSpecialty(specialty);
        return consultation;
    }

}
