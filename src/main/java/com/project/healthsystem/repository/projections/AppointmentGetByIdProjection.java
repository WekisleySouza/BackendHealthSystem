package com.project.healthsystem.repository.projections;

import com.project.healthsystem.model.Priority;
import com.project.healthsystem.model.ServiceTypes;
import com.project.healthsystem.model.Status;

import java.time.LocalDateTime;

public interface AppointmentGetByIdProjection {

    long getId();
    Status getStatus();
    String getNotes();
    Priority getPriorit();
    LocalDateTime getScheduledAt();
    LocalDateTime getCreatedAt();

    ServiceTypeInfo getServiceType();
    ProfessionalInfo getProfessional();
    EmployeeInfo getEmployee();
    PatientInfo getPatient();

    interface ServiceTypeInfo {
        long getId();
        String getName();
        ServiceTypes getType();
    }

    interface ProfessionalInfo {
        long getId();
        PersonInfo getPerson();
    }

    interface EmployeeInfo {
        long getId();
        PersonInfo getPerson();
    }

    interface PatientInfo {
        long getId();
        PersonInfo getPerson();
    }

    interface PersonInfo {
        String getName();
    }
}
