package com.project.healthsystem.repository.datasus;

import com.project.healthsystem.model.datasus_data.sigtap.SigtapSubGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SigtapSubGroupRepository extends JpaRepository<SigtapSubGroup, Long>, JpaSpecificationExecutor<SigtapSubGroup> {
}
