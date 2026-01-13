package com.project.healthsystem.repository;

import com.project.healthsystem.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {

    boolean existsByPersonCpf(String cpf);
}
