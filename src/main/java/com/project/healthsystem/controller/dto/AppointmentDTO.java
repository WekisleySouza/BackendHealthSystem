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
    @NotNull(message = "O status é obrigatório!")
    private Long statusId;

    private Long professionalId;

    @NotNull(message = "O funcionário responsável é obrigatório!")
    private Long employeeId;

    @NotNull(message = "A pessoa é obrigatória!")
    private Long personId;

    private Long serviceTypeId;

    private String notes;

    private LocalDateTime scheduledAt;

    private LocalDateTime createdAt;

    private String priority;

    public AppointmentDTO(Appointment appointment){
        this.id = appointment.getId();
        this.statusId = appointment.getStatusId();
        this.professionalId = appointment.getProfessionalId();
        this.personId = appointment.getPersonId();
        this.serviceTypeId = appointment.getServiceTypeId();

        this.notes = appointment.getNotes();
        this.scheduledAt = appointment.getScheduledAt();
        this.priority = appointment.getPriorit();
    }

    public Appointment mappingToConsultation(Status status, ServiceType specialty, Person person, Employee employee){
        Appointment appointment = new Appointment(
                this.notes,
                this.scheduledAt,
                this.priority
        );
        appointment.setStatus(status);
        appointment.setPerson(person);
        appointment.setEmployee(employee);
        appointment.setServiceType(specialty);
        return appointment;
    }
}
