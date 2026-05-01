package com.project.healthsystem.repository;

import com.project.healthsystem.model.Instituition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstituitionRepository extends JpaRepository<Instituition, Long>, JpaSpecificationExecutor<Instituition> {
}
