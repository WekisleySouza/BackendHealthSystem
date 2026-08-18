package com.project.healthsystem.repository.datasus;

import com.project.healthsystem.model.datasus_data.sigtap.SigtapProcedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SigtapProcedureRepository extends JpaRepository<SigtapProcedure, Long>, JpaSpecificationExecutor<SigtapProcedure> {
}
