package com.project.healthsystem.repository.projections.simplified_lists;

public interface PatientSimplifiedInfoProjection {
    Long getId();
    PersonInfo getPerson();

    interface PersonInfo {
        String getName();
    }
}
