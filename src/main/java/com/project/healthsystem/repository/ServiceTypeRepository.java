package com.project.healthsystem.repository;

import com.project.healthsystem.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long>, JpaSpecificationExecutor<ServiceType> {
}
