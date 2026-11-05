package com.project.healthsystem.repository.datasus;

import com.project.healthsystem.model.datasus_data.cid.Cid10;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface Cid10Repository extends JpaRepository<Cid10, Long>, JpaSpecificationExecutor<Cid10> {
    boolean existsByCode(String code);
}
