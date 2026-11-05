package com.project.healthsystem.repository;

import com.project.healthsystem.model.AppointmentStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

public interface AppointmentStatusHistoryRepository extends JpaRepository<AppointmentStatusHistory, Long> {
    @EntityGraph(attributePaths = "changedBy")
    Page<AppointmentStatusHistory> findByAppointmentId(long appointmentId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM AppointmentStatusHistory h WHERE h.appointment.id = :appointmentId")
    void deleteByAppointmentId(long appointmentId);
}
