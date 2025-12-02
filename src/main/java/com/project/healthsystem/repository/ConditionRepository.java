package com.project.healthsystem.repository;

import com.project.healthsystem.model.Condition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConditionRepository extends JpaRepository<Condition, Long>, JpaSpecificationExecutor<Condition> {

}
