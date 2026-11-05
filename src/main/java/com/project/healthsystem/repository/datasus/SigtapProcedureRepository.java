package com.project.healthsystem.repository.datasus;

import com.project.healthsystem.model.datasus_data.sigtap.SigtapProcedure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface SigtapProcedureRepository extends JpaRepository<SigtapProcedure, Long>, JpaSpecificationExecutor<SigtapProcedure> {
    @Override
    @EntityGraph(attributePaths = {"organization", "organization.subGroup", "organization.subGroup.group"})
    Page<SigtapProcedure> findAll(Specification<SigtapProcedure> specification, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"organization", "organization.subGroup", "organization.subGroup.group"})
    Optional<SigtapProcedure> findById(Long id);
}
