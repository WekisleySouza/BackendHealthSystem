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

    ProfessionalInfo getResponsibleProfessional();
    ProfessionalInfo getRequestingProfessional();
    EmployeeInfo getEmployee();
    PatientInfo getPatient();
    ServiceTypeInfo getServiceType();

    default String getResponsibleProfessionalNameSafe(){
        return getResponsibleProfessional() != null
                ? getResponsibleProfessional().getPerson() != null
                  ? getResponsibleProfessional().getPerson().getName()
                  : null
                : null;
    }

    default String getRequestingProfessionalNameSafe(){
        return getRequestingProfessional() != null
                ? getRequestingProfessional().getPerson() != null
                  ? getRequestingProfessional().getPerson().getName()
                  : null
                : null;
    }

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