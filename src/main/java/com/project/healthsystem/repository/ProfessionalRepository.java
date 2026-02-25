package com.project.healthsystem.repository;

import com.project.healthsystem.model.Professional;
import com.project.healthsystem.repository.projections.simplified_lists.ProfessionalSimplifiedInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProfessionalRepository extends JpaRepository<Professional, Long>, JpaSpecificationExecutor<Professional> {

    Page<ProfessionalSimplifiedInfoProjection> getAllBy(Pageable page);

    boolean existsByPersonCpf(String cpf);

    @Query("SELECT COUNT(p) FROM Professional p")
    Long countProfessionals();
}
