package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.*;
import jakarta.validation.constraints.NotNull;
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
public class AppointmentDTO {

    private long id;
    @NotNull
    private long statusId;
    private long professionalId;
    @NotNull
    private long employeeId;
    @NotNull
    private long personId;
    private long serviceTypeId;

    private String notes;
    private String scheduledAt;
    private String createdAt;
    private String priorit;

    public AppointmentDTO(Appointment appointment){
        this.id = appointment.getId();
        this.statusId = appointment.getStatusId();
        this.professionalId = appointment.getProfessionalId();
        this.personId = appointment.getPersonId();
        this.serviceTypeId = appointment.getServiceTypeId();

        this.notes = appointment.getNotes();
        this.scheduledAt = appointment.getScheduledAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        this.priorit = appointment.getPriorit();
    }

    public Appointment mappingToConsultation(Status status, ServiceType specialty){
        Appointment appointment = new Appointment(
                this.notes,
                LocalDateTime.parse(this.scheduledAt, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                this.priorit
        );
        appointment.setStatus(status);
        appointment.setServiceType(specialty);
        return appointment;
    }

    public Appointment mappingToConsultation(Status status, ServiceType specialty, Professional professional){
        Appointment appointment = new Appointment(
                this.notes,
                LocalDateTime.parse(this.scheduledAt, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                this.priorit
        );
        appointment.setStatus(status);
        appointment.setProfessional(professional);
        appointment.setServiceType(specialty);
        return appointment;
    }

    public Appointment mappingToConsultation(Status status, ServiceType specialty, Person person){
        Appointment appointment = new Appointment(
                this.notes,
                LocalDateTime.parse(this.scheduledAt, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                this.priorit
        );
        appointment.setStatus(status);
        appointment.setPerson(person);
        appointment.setServiceType(specialty);
        return appointment;
    }

    public Appointment mappingToConsultation(
            Status status,
            ServiceType specialty,
            Professional professional,
            Person person
    ){
        Appointment appointment = new Appointment(
                this.notes,
                LocalDateTime.parse(this.scheduledAt, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                this.priorit
        );
        appointment.setStatus(status);
        appointment.setProfessional(professional);
        appointment.setPerson(person);
        appointment.setServiceType(specialty);
        return appointment;
    }
}
