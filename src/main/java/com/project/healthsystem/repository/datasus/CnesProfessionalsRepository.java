package com.project.healthsystem.repository.datasus;

import com.project.healthsystem.model.datasus_data.cnes.CnesProfessional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CnesProfessionalsRepository extends JpaRepository<CnesProfessional, Long>, JpaSpecificationExecutor<CnesProfessional> {
}
