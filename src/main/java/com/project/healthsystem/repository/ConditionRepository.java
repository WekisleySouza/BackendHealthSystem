package com.project.healthsystem.repository;

import com.project.healthsystem.model.Condition;
import com.project.healthsystem.repository.projections.PatientInfoConditionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ConditionRepository extends JpaRepository<Condition, Long>, JpaSpecificationExecutor<Condition> {
}
