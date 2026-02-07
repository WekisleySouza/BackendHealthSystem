package com.project.healthsystem.repository.projections;

import java.util.List;

public interface PatientInfoConditionsProjection {

    List<PatientInfoConditionProjection> getConditions();

}
