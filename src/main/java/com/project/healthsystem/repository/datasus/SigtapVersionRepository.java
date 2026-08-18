package com.project.healthsystem.repository.datasus;

import com.project.healthsystem.model.datasus_data.sigtap.SigtapVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SigtapVersionRepository extends JpaRepository<SigtapVersion, Long>, JpaSpecificationExecutor<SigtapVersion> {
  
  Optional<SigtapVersion> findFirstByOrderByCompetenceDesc();
}
