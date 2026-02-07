package com.project.healthsystem.repository;

import com.project.healthsystem.model.Patient;
import com.project.healthsystem.repository.projections.PatientInfoAgentProjection;
import com.project.healthsystem.repository.projections.PatientInfoAppointmentsProjection;
import com.project.healthsystem.repository.projections.PatientInfoConditionsProjection;
import com.project.healthsystem.repository.projections.PatientInfoResponsibleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

    Optional<PatientInfoResponsibleProjection> findResponsibleById(long patientId);

    Optional<PatientInfoAgentProjection> findAgentById(long patientId);

    Optional<PatientInfoConditionsProjection> findConditionsById(long patientId);

    Optional<PatientInfoAppointmentsProjection> findAppointmentsById(long patientId);

    Optional<Patient> findByPersonCpf(String cpf);

    Patient findByCns(String cns);

    List<Patient> findByPersonName(String name);

    boolean existsByPersonCpf(String cpf);


}
