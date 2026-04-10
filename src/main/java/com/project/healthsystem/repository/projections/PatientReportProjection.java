package com.project.healthsystem.repository.projections;

import com.project.healthsystem.model.ServiceTypes;
import com.project.healthsystem.model.Status;

import java.time.LocalDateTime;

public interface PatientReportProjection {
    PatientInfo getPatient();
    ProfessionalInfo getProfessional();
    ServiceTypeInfo getServiceType();

    LocalDateTime getScheduledAt();
    Status getStatus();
    String getPriorit();


    interface PatientInfo{
        PersonInfo getPerson();
        String getMotherName();

    }

    interface ProfessionalInfo{
        PersonInfo getPerson();

    }

    interface PersonInfo{
        String getName();
    }

    interface ServiceTypeInfo{
        String getName();
        ServiceTypes getType();
    }
}
