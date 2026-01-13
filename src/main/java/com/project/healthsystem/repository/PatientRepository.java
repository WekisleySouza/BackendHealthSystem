package com.project.healthsystem.repository;

import com.project.healthsystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

    Optional<Patient> findByPersonCpf(String cpf);

    Patient findByCns(String cns);

    List<Patient> findByPersonName(String name);

    boolean existsByPersonCpf(String cpf);
}
