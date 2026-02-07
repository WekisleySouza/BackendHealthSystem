package com.project.healthsystem.repository.projections;

import com.project.healthsystem.model.Status;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface PatientInfoAppointmentProjection {

    long getId();
    Status getStatus();
    String getNotes();
    String getPriorit();
    LocalDateTime getScheduledAt();
    LocalDateTime getCreatedAt();

    @Value("#{target.professional.person.name}")
    String professionalName();
    @Value("#{target.employee.person.name}")
    String employeeName();
    @Value("#{target.patient.person.name}")
    String patientName();
    @Value("#{target.serviceType.name}")
    String serviceTypeName();
}
