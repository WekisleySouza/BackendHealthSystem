package com.project.healthsystem.repository;

import com.project.healthsystem.model.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AgreementRepository extends JpaRepository<Agreement, Long>, JpaSpecificationExecutor<Agreement> {

    boolean existsByName(String name);
}
