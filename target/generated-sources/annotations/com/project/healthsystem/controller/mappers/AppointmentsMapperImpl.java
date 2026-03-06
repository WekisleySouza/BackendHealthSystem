package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AppointmentResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.model.Appointment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-06T17:38:30-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
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
        appointment.setScheduledAt( dto.getScheduledAt() );

        appointment.setStatus( com.project.healthsystem.model.Status.fromLabel(dto.getStatus()) );
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
        appointmentResponseDTO.setScheduledAt( entity.getScheduledAt() );
        appointmentResponseDTO.setCreatedAt( entity.getCreatedAt() );

        appointmentResponseDTO.setProfessionalName( entity.getProfessionalName() );
        appointmentResponseDTO.setEmployeeName( entity.getEmployeeName() );
        appointmentResponseDTO.setPatientName( entity.getPatientName() );
        appointmentResponseDTO.setServiceTypeName( entity.getServiceTypeName() );
        appointmentResponseDTO.setServiceType( entity.getServiceType() );
        appointmentResponseDTO.setStatus( entity.getStatus().getLabel() );
        appointmentResponseDTO.setPriority( entity.getPriorit().getLabel() );

        return appointmentResponseDTO;
    }
}
