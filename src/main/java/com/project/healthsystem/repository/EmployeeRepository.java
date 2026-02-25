package com.project.healthsystem.repository;

import com.project.healthsystem.model.Employee;
import com.project.healthsystem.repository.projections.simplified_lists.EmployeeSimplifiedInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Page<EmployeeSimplifiedInfoProjection> getAllBy(Pageable page);

    Optional<Employee> findByPersonCpf(String cpf);

    boolean existsByPersonCpf(String cpf);
}
