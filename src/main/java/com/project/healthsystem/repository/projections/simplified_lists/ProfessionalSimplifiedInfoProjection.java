package com.project.healthsystem.repository.projections.simplified_lists;

public interface ProfessionalSimplifiedInfoProjection {
    Long getId();
    PersonInfo getPerson();

    interface PersonInfo {
        String getName();
    }
}
