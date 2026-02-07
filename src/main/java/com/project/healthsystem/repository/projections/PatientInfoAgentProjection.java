package com.project.healthsystem.repository.projections;

public interface PatientInfoAgentProjection {
    AgentInfo getAgent();

    interface AgentInfo{
        long getId();
        PersonInfo getPerson();
    }

    interface PersonInfo{
        String getName();
    }
}
