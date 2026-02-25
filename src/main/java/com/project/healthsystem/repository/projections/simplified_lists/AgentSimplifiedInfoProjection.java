package com.project.healthsystem.repository.projections.simplified_lists;

public interface AgentSimplifiedInfoProjection {

    Long getId();
    PersonInfo getPerson();

    interface PersonInfo{
        String getName();
    }
}
