package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AppointmentRequestDTO;
import com.project.healthsystem.controller.dto.AppointmentResponseDTO;
import com.project.healthsystem.model.Appointment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-13T18:09:26-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class AppointmentsMapperImpl extends AppointmentsMapper {

    @Override
    public Appointment toEntity(AppointmentRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Appointment appointment = new Appointment();

        appointment.setNotes( dto.getNotes() );
        appointment.setScheduledAt( dto.getScheduledAt() );

        appointment.setStatus( com.project.healthsystem.model.Status.fromLabel(dto.getStatus()) );

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

        appointmentResponseDTO.setProfessionalId( entity.getProfessionalId() );
        appointmentResponseDTO.setEmployeeId( entity.getEmployeeId() );
        appointmentResponseDTO.setPatientId( entity.getPatientId() );
        appointmentResponseDTO.setServiceTypeId( entity.getServiceTypeId() );
        appointmentResponseDTO.setStatus( entity.getStatus() );

        return appointmentResponseDTO;
    }
}
