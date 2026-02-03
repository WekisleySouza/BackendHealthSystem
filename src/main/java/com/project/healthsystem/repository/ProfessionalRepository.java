package com.project.healthsystem.repository;

import com.project.healthsystem.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProfessionalRepository extends JpaRepository<Professional, Long>, JpaSpecificationExecutor<Professional> {
    public boolean existsByPersonCpf(String cpf);

    @Query("SELECT COUNT(p) FROM Professional p")
    Long countProfessionals();
}
