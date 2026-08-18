package com.project.healthsystem.repository.datasus;

import com.project.healthsystem.model.datasus_data.sigtap.SigtapGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SigtapGroupRepository extends JpaRepository<SigtapGroup, Long>, JpaSpecificationExecutor<SigtapGroup> {
}
