package com.project.healthsystem.repository;

import com.project.healthsystem.model.Condition;
import com.project.healthsystem.repository.projections.simplified_lists.ConditionSimplifiedInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConditionRepository extends JpaRepository<Condition, Long>, JpaSpecificationExecutor<Condition> {
    Page<ConditionSimplifiedInfoProjection> getAllBy(Pageable page);
}
