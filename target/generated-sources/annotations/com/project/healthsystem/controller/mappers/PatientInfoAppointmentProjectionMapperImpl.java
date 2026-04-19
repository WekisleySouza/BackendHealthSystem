package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.PatientInfoAppointmentResponseDTO;
import com.project.healthsystem.repository.projections.PatientInfoAppointmentProjection;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T18:57:03-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class PatientInfoAppointmentProjectionMapperImpl extends PatientInfoAppointmentProjectionMapper {

    @Override
    public PatientInfoAppointmentResponseDTO toDto(PatientInfoAppointmentProjection entity) {
        if ( entity == null ) {
            return null;
        }

        long id = 0L;
        String notes = null;
        LocalDateTime scheduledAt = null;
        LocalDateTime createdAt = null;

        id = entity.getId();
        notes = entity.getNotes();
        scheduledAt = entity.getScheduledAt();
        createdAt = entity.getCreatedAt();

        String responsibleProfessionalName = entity.getResponsibleProfessional().getPerson().getName();
        String requestingProfessionalName = entity.getRequestingProfessional().getPerson().getName();
        String employeeName = entity.getEmployee().getPerson().getName();
        String patientName = entity.getPatient().getPerson().getName();
        String serviceTypeName = entity.getServiceType().getName();
        String status = entity.getStatus().getLabel();
        String priorit = entity.getPriorit().getLabel();

        PatientInfoAppointmentResponseDTO patientInfoAppointmentResponseDTO = new PatientInfoAppointmentResponseDTO( id, responsibleProfessionalName, requestingProfessionalName, employeeName, patientName, status, serviceTypeName, notes, priorit, scheduledAt, createdAt );

        return patientInfoAppointmentResponseDTO;
    }
}
