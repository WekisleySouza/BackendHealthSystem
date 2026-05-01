package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AppointmentResponseDTO;
import com.project.healthsystem.model.Appointment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-30T18:30:23-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class AppointmentsMapperImpl extends AppointmentsMapper {

    @Override
    public Appointment toEntity(AppointmentRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Appointment appointment = new Appointment();

        appointment.setCreatedAt( dto.getCreatedAt() );
        appointment.setNotes( dto.getNotes() );
        appointment.setReturn( dto.isReturn() );
        appointment.setSchedulingForecast( dto.getSchedulingForecast() );
        appointment.setScheduledAt( dto.getScheduledAt() );

        appointment.setStatus( com.project.healthsystem.model.Status.fromLabel(dto.getStatus()) );
        appointment.setAgreements( com.project.healthsystem.model.Agreements.fromLabel(dto.getAgreements()) );
        appointment.setPriorit( com.project.healthsystem.model.Priority.fromLabel(dto.getPriority()) );

        return appointment;
    }

    @Override
    public AppointmentResponseDTO toDto(Appointment entity) {
        if ( entity == null ) {
            return null;
        }

        AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO();

        appointmentResponseDTO.setId( entity.getId() );
        appointmentResponseDTO.setNotes( entity.getNotes() );
        appointmentResponseDTO.setReturn( entity.isReturn() );
        appointmentResponseDTO.setScheduledAt( entity.getScheduledAt() );
        appointmentResponseDTO.setSchedulingForecast( entity.getSchedulingForecast() );
        appointmentResponseDTO.setCreatedAt( entity.getCreatedAt() );

        appointmentResponseDTO.setResponsibleProfessionalName( entity.getResponsibleProfessionalName() );
        appointmentResponseDTO.setRequestingProfessionalName( entity.getRequestingProfessionalName() );
        appointmentResponseDTO.setEmployeeName( entity.getEmployeeName() );
        appointmentResponseDTO.setPatientName( entity.getPatientName() );
        appointmentResponseDTO.setServiceTypeName( entity.getServiceTypeName() );
        appointmentResponseDTO.setServiceType( entity.getServiceType() );
        appointmentResponseDTO.setCategoryGroupName( entity.getServiceTypeCategoryGroupName() );
        appointmentResponseDTO.setStatus( entity.getStatus().getLabel() );
        appointmentResponseDTO.setAgreements( entity.getAgreements().getLabel() );
        appointmentResponseDTO.setPriority( entity.getPriorit().getLabel() );

        return appointmentResponseDTO;
    }
}
