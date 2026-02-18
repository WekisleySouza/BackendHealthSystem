package com.project.healthsystem.repository;

import com.project.healthsystem.model.Patient;
import com.project.healthsystem.repository.projections.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

    List<PatientSimplifiedInfoProjection> findAllBy();

    Optional<PatientInfoResponsibleProjection> findResponsibleById(long patientId);

    Optional<PatientInfoAgentProjection> findAgentById(long patientId);

    Optional<PatientInfoConditionsProjection> findConditionsById(long patientId);

    Optional<Patient> findByPersonCpf(String cpf);

    Patient findByCns(String cns);

    List<Patient> findByPersonName(String name);

    boolean existsByPersonCpf(String cpf);


}
