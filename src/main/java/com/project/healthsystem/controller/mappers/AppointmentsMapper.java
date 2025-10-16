package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AppointmentDTO;
import com.project.healthsystem.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AppointmentsMapper {

    public abstract Appointment toEntity(AppointmentDTO dto);

    @Mapping(target = "statusId", expression = "java(entity.getStatusId())")
    @Mapping(target = "professionalId", expression = "java(entity.getProfessionalId())")
    @Mapping(target = "employeeId", expression = "java(entity.getEmployeeId())")
    @Mapping(target = "personId", expression = "java(entity.getPersonId())")
    @Mapping(target = "serviceTypeId", expression = "java(entity.getServiceTypeId())")
    public abstract AppointmentDTO toDto(Appointment entity);

    public Appointment toEntityWhenHasId(Appointment entity, AppointmentDTO dto) {
        Appointment newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}