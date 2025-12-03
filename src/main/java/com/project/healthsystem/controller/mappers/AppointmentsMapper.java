package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AppointmentRequestDTO;
import com.project.healthsystem.controller.dto.AppointmentResponseDTO;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.model.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AppointmentsMapper {

    @Mapping(target = "status", expression = "java(com.project.healthsystem.model.Status.fromLabel(dto.getStatus()))")
    public abstract Appointment toEntity(AppointmentRequestDTO dto);

    @Mapping(target = "professionalId", expression = "java(entity.getProfessionalId())")
    @Mapping(target = "employeeId", expression = "java(entity.getEmployeeId())")
    @Mapping(target = "personId", expression = "java(entity.getPersonId())")
    @Mapping(target = "serviceTypeId", expression = "java(entity.getServiceTypeId())")
    @Mapping(target = "status", expression = "java(entity.getStatus().getLabel())")
    public abstract AppointmentResponseDTO toDto(Appointment entity);

    public Appointment toEntityWhenHasId(Appointment entity, AppointmentRequestDTO dto) {
        entity.setStatus(Status.fromLabel(dto.getStatus()));
        entity.setNotes(dto.getNotes());
        entity.setScheduledAt(dto.getScheduledAt());
        return entity;
    }
}