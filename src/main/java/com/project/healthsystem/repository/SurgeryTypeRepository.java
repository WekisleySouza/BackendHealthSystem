package com.project.healthsystem.repository;

import com.project.healthsystem.model.SurgeryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SurgeryTypeRepository extends JpaRepository<SurgeryType, Long>, JpaSpecificationExecutor<SurgeryType> {
}
