package com.project.healthsystem.repository;

import com.project.healthsystem.model.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SurgeryRepository extends JpaRepository<Surgery, Long>, JpaSpecificationExecutor<Surgery> {
}
