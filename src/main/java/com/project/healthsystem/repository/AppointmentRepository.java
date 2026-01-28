package com.project.healthsystem.repository;

import com.project.healthsystem.controller.dto.AppointmentReportResponseDTO;
import com.project.healthsystem.controller.dto.AppointmentStatusCountResponseDTO;
import com.project.healthsystem.model.Appointment;
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
            prof.person.name
        )
        FROM Appointment a
        JOIN a.patient p
        JOIN a.professional prof
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

}
