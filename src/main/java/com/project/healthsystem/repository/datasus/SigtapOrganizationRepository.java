package com.project.healthsystem.repository.datasus;

import com.project.healthsystem.model.datasus_data.sigtap.SigtapOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SigtapOrganizationRepository extends JpaRepository<SigtapOrganization, Long>, JpaSpecificationExecutor<SigtapOrganization> {
}
