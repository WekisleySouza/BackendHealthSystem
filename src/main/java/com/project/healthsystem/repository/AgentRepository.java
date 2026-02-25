package com.project.healthsystem.repository;

import com.project.healthsystem.model.Agent;
import com.project.healthsystem.repository.projections.simplified_lists.AgentSimplifiedInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {

    boolean existsByPersonCpf(String cpf);

    Page<AgentSimplifiedInfoProjection> findAllBy(Pageable page);
}
