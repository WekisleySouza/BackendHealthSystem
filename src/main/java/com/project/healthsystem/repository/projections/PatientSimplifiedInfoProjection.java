package com.project.healthsystem.repository.projections;

public interface PatientSimplifiedInfoProjection {

    long getId();
    String getMotherName();
    PersonInfo getPerson();

    interface PersonInfo{
        String getCpf();
        String getName();
    }
}
