package com.project.healthsystem.repository.datasus;

import com.project.healthsystem.model.datasus_data.ciap.Ciap2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface Ciap2Repository extends JpaRepository<Ciap2, Long>, JpaSpecificationExecutor<Ciap2> {
    boolean existsByCode(String code);
}
