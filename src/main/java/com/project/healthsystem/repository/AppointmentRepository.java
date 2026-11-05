package com.project.healthsystem.repository;

import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.dto.patient_page_responses.PatientAppointmentResponseDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberExamsByStatusDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberSpecialtiesByStatusDTO;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.repository.projections.AppointmentGetByIdProjection;
import com.project.healthsystem.repository.projections.PatientInfoAppointmentProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    Optional<AppointmentGetByIdProjection> findAppointmentById(long id);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.id = :id")
    Optional<Appointment> findByIdForUpdate(long id);


    @Modifying
    @Transactional
    @Query("""
    UPDATE Appointment a
    SET a.serviceType.id = :newId
    WHERE a.serviceType.id = :oldId
""")
    int updateServiceTypeId(@Param("oldId") long oldId, @Param("newId") long newId);

    boolean existsByServiceTypeId(long id);

    @Query("""
        SELECT new com.project.healthsystem.controller.dto.patient_page_responses.PatientAppointmentResponseDTO(
            a.notes,
            a.priorit,
            a.status,
            agr.name,
            a.isReturn,
            a.schedulingForecast,
            a.scheduledAt,
    
            i.name,
            i.cep,
            i.cityName,
            i.address,
            i.phone,
            i.linkLogo,
    
            st.name,
            st.value,
    
            rpp.name,
            respp.name
        )
        FROM Appointment a
        JOIN a.agreement agr
        JOIN a.instituition i
        JOIN a.serviceType st
        JOIN a.requestingProfessional rp
        JOIN rp.person rpp
        JOIN a.responsibleProfessional resp
        JOIN resp.person respp
        WHERE a.patient.person.id = :personId
    """)
    Page<PatientAppointmentResponseDTO> getAllAppointmentsOfPatient(
        long personId,
        Pageable pageable
    );

    List<PatientInfoAppointmentProjection> findByPatient_Id(long patientId);

    @Query("""
        SELECT a.id FROM Appointment a
        WHERE a.status IN (
            com.project.healthsystem.model.Status.SCHEDULED,
            com.project.healthsystem.model.Status.OVERDUE,
            com.project.healthsystem.model.Status.PENDING_SCHEDULING
        ) AND (
            (a.scheduledAt IS NULL AND a.status <> com.project.healthsystem.model.Status.PENDING_SCHEDULING)
            OR (a.scheduledAt < :now AND a.status <> com.project.healthsystem.model.Status.OVERDUE)
            OR (a.scheduledAt > :now AND a.status <> com.project.healthsystem.model.Status.SCHEDULED)
        )
        ORDER BY a.id
        """)
    List<Long> findAutomaticStatusChangeIds(LocalDateTime now);
}
