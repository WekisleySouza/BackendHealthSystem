package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.PatientInfoAppointmentResponseDTO;
import com.project.healthsystem.repository.projections.PatientInfoAppointmentProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PatientInfoAppointmentProjectionMapper {

    @Mapping(target = "professionalName", expression = "java(entity.professionalName())")
    @Mapping(target = "employeeName", expression = "java(entity.employeeName())")
    @Mapping(target = "patientName", expression = "java(entity.patientName())")
    @Mapping(target = "serviceTypeName", expression = "java(entity.serviceTypeName())")
    @Mapping(target = "status", expression = "java(entity.getStatus().getLabel())")
    @Mapping(target = "priorit", expression = "java(entity.getPriorit().getLabel())")
    public abstract PatientInfoAppointmentResponseDTO toDto(PatientInfoAppointmentProjection entity);
}
