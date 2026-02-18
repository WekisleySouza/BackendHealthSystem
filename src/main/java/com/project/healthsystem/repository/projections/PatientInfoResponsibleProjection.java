package com.project.healthsystem.repository.projections;

import org.springframework.beans.factory.annotation.Value;

public interface PatientInfoResponsibleProjection {

    @Value("#{target.responsible != null ? target.responsible.person.id : null}")
    Long getResponsibleId();

    @Value("#{target.responsible != null ? target.responsible.person.name : null}")
    String getResponsibleName();
}
