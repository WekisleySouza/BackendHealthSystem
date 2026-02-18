package com.project.healthsystem.repository.projections;

import com.project.healthsystem.model.Priority;
import com.project.healthsystem.model.Status;

import java.time.LocalDateTime;

public interface PatientInfoAppointmentProjection {

    long getId();
    Status getStatus();
    String getNotes();
    Priority getPriorit();
    LocalDateTime getScheduledAt();
    LocalDateTime getCreatedAt();

    ProfessionalInfo getProfessional();
    EmployeeInfo getEmployee();
    PatientInfo getPatient();
    ServiceTypeInfo getServiceType();

    interface ProfessionalInfo {
        PersonInfo getPerson();
    }

    interface EmployeeInfo {
        PersonInfo getPerson();
    }

    interface PatientInfo {
        PersonInfo getPerson();
    }

    interface ServiceTypeInfo {
        String getName();
    }

    interface PersonInfo{
        String getName();
    }
}
