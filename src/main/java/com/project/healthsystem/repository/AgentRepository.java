package com.project.healthsystem.repository;

import com.project.healthsystem.model.Agent;
import com.project.healthsystem.repository.projections.PatientInfoAgentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {

    boolean existsByPersonCpf(String cpf);
}
