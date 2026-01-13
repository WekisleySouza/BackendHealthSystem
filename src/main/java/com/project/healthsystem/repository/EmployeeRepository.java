package com.project.healthsystem.repository;

import com.project.healthsystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByPersonCpf(String cpf);

    boolean existsByPersonCpf(String cpf);
}
