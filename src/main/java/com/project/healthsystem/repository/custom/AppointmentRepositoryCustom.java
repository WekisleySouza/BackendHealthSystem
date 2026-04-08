package com.project.healthsystem.repository.custom;

import com.project.healthsystem.controller.dto.reports_patients.AppointmentSummaryDTO;
import com.project.healthsystem.model.Appointment;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface AppointmentRepositoryCustom {
    List<AppointmentSummaryDTO> getSummary(Specification<Appointment> spec);
}
