package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.PatientInfoAppointmentResponseDTO;
import com.project.healthsystem.repository.projections.PatientInfoAppointmentProjection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class PatientInfoAppointmentProjectionMapper {

    public abstract PatientInfoAppointmentResponseDTO toDto(PatientInfoAppointmentProjection entity);
}
