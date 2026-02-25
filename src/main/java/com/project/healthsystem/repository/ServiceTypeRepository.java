package com.project.healthsystem.repository;

import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.repository.projections.simplified_lists.ServiceTypeSimplifiedInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long>, JpaSpecificationExecutor<ServiceType> {

    Page<ServiceTypeSimplifiedInfoProjection> getAllBy(Pageable page);

}
