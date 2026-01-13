package com.project.healthsystem.repository;

import com.project.healthsystem.model.Role;
import com.project.healthsystem.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RoleRepository extends JpaRepository <Role, Long>, JpaSpecificationExecutor<Role> {

    boolean existsBy();

    Optional<Role> findByRole(Roles role);
}
