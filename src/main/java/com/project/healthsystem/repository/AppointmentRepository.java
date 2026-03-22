package com.project.healthsystem.repository;

import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.dto.reports_professional.NumberAppointmentsByStatusAndProfessionalDTO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    Optional<AppointmentGetByIdProjection> findAppointmentById(long id);

    @Query("""
        SELECT new com.project.healthsystem.controller.dto.AppointmentReportResponseDTO(
            p.person.name,
            p.motherName,
            a.scheduledAt,
            a.status,
            prof.person.name,
            a.priorit,
            st.name,
            st.type
        )
        FROM Appointment a
        JOIN a.patient p
        JOIN a.professional prof
        JOIN a.serviceType st
        ORDER BY a.scheduledAt DESC
    """)
    Page<AppointmentReportResponseDTO> findAppointmentReport(Pageable pageable);

    @Query("""
        SELECT new com.project.healthsystem.controller.dto.AppointmentStatusCountResponseDTO(
            a.status,
            COUNT(a)
        )
        FROM Appointment a
        GROUP BY a.status
    """)
    List<AppointmentStatusCountResponseDTO> countByStatus();

    @Query("""
        SELECT new com.project.healthsystem.controller.dto.reports_professional.NumberAppointmentsByStatusAndProfessionalDTO(
            prof.id,
            prof.person.name,
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.SCHEDULED THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.COMPLETED THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.CANCELED THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.NO_SHOW THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.OVERDUE THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.PENDING_SCHEDULING THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.serviceType.type = com.project.healthsystem.model.ServiceTypes.EXAM THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.serviceType.type = com.project.healthsystem.model.ServiceTypes.SPECIALTY THEN 1 ELSE 0 END),
            COUNT(a)
        )
        FROM Appointment a
        JOIN a.professional prof
        GROUP BY prof.id, prof.person.name
        ORDER BY prof.person.name
    """)
    Page<NumberAppointmentsByStatusAndProfessionalDTO> countAppointmentsGroupedByProfessional(Pageable pageable);

    @Query("""
        SELECT new com.project.healthsystem.controller.dto.reports_specialties.NumberSpecialtiesByStatusDTO(
            st.id,
            st.name,
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.SCHEDULED THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.COMPLETED THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.CANCELED THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.NO_SHOW THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.OVERDUE THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.PENDING_SCHEDULING THEN 1 ELSE 0 END),
            COUNT(a)
        )
        FROM Appointment a
        JOIN a.serviceType st
        WHERE st.type = com.project.healthsystem.model.ServiceTypes.SPECIALTY
        GROUP BY st.id, st.name
        ORDER BY st.name
    """)
    Page<NumberSpecialtiesByStatusDTO> countSpecialtiesByStatus(Pageable pageable);

    @Query("""
        SELECT new com.project.healthsystem.controller.dto.reports_specialties.NumberExamsByStatusDTO(
            st.id,
            st.name,
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.SCHEDULED THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.COMPLETED THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.CANCELED THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.NO_SHOW THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.OVERDUE THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = com.project.healthsystem.model.Status.PENDING_SCHEDULING THEN 1 ELSE 0 END),
            COUNT(a)
        )
        FROM Appointment a
        JOIN a.serviceType st
        WHERE st.type = com.project.healthsystem.model.ServiceTypes.EXAM
        GROUP BY st.id, st.name
        ORDER BY st.name
    """)
    Page<NumberExamsByStatusDTO> countExamsByStatus(Pageable pageable);

    @Query("""
    SELECT new com.project.healthsystem.controller.dto.ReportAppointmentCountByServiceTypeByProfessionalResponse(
            prof.id,
            prof.person.name,
            st.name,
            a.scheduledAt,
            COUNT(a)
        )
        FROM Appointment a
        JOIN a.professional prof
        JOIN a.serviceType st
        GROUP BY prof.id, prof.person.name, st.name
    """)
    Page<ReportAppointmentCountByServiceTypeByProfessionalResponse> countByProfessionalAndServiceType(Pageable pageable);

    List<PatientInfoAppointmentProjection> findByPatient_Id(long patientId);

    @Modifying
    @Query("""
        UPDATE Appointment a
        SET a.status = com.project.healthsystem.model.Status.OVERDUE
        WHERE a.scheduledAt IS NOT NULL
          AND a.status NOT IN (
              com.project.healthsystem.model.Status.COMPLETED,
              com.project.healthsystem.model.Status.NO_SHOW,
              com.project.healthsystem.model.Status.CANCELED
          )
          AND a.scheduledAt < :now
    """)
    void updateToOverdue(LocalDateTime now);

    @Modifying
    @Query("""
        UPDATE Appointment a
        SET a.status = com.project.healthsystem.model.Status.SCHEDULED
        WHERE a.scheduledAt IS NOT NULL
          AND a.status NOT IN (
              com.project.healthsystem.model.Status.COMPLETED,
              com.project.healthsystem.model.Status.NO_SHOW,
              com.project.healthsystem.model.Status.CANCELED
          )
          AND a.scheduledAt > :now
    """)
    void updateToScheduled(LocalDateTime now);

    @Modifying
    @Query("""
        UPDATE Appointment a
        SET a.status = com.project.healthsystem.model.Status.PENDING_SCHEDULING
        WHERE a.scheduledAt IS NULL
            AND a.status NOT IN (
                  com.project.healthsystem.model.Status.COMPLETED,
                  com.project.healthsystem.model.Status.NO_SHOW,
                  com.project.healthsystem.model.Status.CANCELED
            )
    """)
    void updateToPending();

}
