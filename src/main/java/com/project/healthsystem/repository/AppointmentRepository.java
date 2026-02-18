package com.project.healthsystem.repository;

import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.repository.projections.PatientInfoAppointmentProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

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
        SELECT new com.project.healthsystem.controller.dto.ReportAppointmentCountByProfessionalResponseDTO(
            prof.id,
            prof.person.name,
            a.scheduledAt,
            COUNT(a)
        )
        FROM Appointment a
        JOIN a.professional prof
        GROUP BY prof.id, prof.person.name
    """)
    Page<ReportAppointmentCountByProfessionalResponseDTO> countAppointmentsByProfessional(Pageable pageable);

    @Query("""
        SELECT new com.project.healthsystem.controller.dto.ReportAppointmentCountByStatusByProfessionalResponseDTO(
            prof.id,
            prof.person.name,
            a.status,
            a.scheduledAt,
            COUNT(a)
        )
        FROM Appointment a
        JOIN a.professional prof
        GROUP BY prof.id, prof.person.name, a.status
    """)
    Page<ReportAppointmentCountByStatusByProfessionalResponseDTO> countByProfessionalAndStatus(Pageable pageable);

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

}
