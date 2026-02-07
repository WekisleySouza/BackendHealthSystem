package com.project.healthsystem.repository.projections;

public interface PatientInfoResponsibleProjection {

    PersonInfo getPerson();

    interface PersonInfo{
        long getId();
        String getName();
    }
}
