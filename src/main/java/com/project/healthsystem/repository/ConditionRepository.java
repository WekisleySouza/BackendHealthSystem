package com.project.healthsystem.repository;

import com.project.healthsystem.model.Condition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionRepository extends JpaRepository<Condition, Long> {

}
