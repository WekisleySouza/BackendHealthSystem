package com.project.healthsystem.repository.projections;

import org.springframework.beans.factory.annotation.Value;

public interface PatientInfoAgentProjection {

    @Value("#{target.agent != null ? target.agent.person.id : null}")
    Long getAgentId();

    @Value("#{target.agent != null ? target.agent.person.id : null}")
    String getAgentName();
}
