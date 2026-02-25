package com.project.healthsystem.repository.projections.simplified_lists;

public interface EmployeeSimplifiedInfoProjection {
    Long getId();
    PersonInfo getPerson();

    interface PersonInfo {
        String getName();
    }
}
