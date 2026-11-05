package com.project.healthsystem.repository.custom;

import com.project.healthsystem.controller.dto.reports_patients.AppointmentSummaryDTO;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportField;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportRowDTO;
import com.project.healthsystem.model.Appointment;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportStatisticsDTO;

public interface AppointmentRepositoryCustom {
    PatientReportStatisticsDTO findPatientReportStatistics(Specification<Appointment> spec);

    List<AppointmentSummaryDTO> getSummary(Specification<Appointment> spec);

    List<PatientReportRowDTO> findPatientReportRows(
        Specification<Appointment> spec,
        PatientReportField sortField,
        boolean descending,
        int offset,
        int limit
    );
}
