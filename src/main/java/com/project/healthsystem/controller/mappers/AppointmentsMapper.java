package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AppointmentRequestDTO;
import com.project.healthsystem.controller.dto.AppointmentResponseDTO;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.model.Priority;
import com.project.healthsystem.model.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AppointmentsMapper {

    @Mapping(target = "status", expression = "java(com.project.healthsystem.model.Status.fromLabel(dto.getStatus()))")
    @Mapping(target = "priorit", expression = "java(com.project.healthsystem.model.Priority.fromLabel(dto.getPriority()))")
    public abstract Appointment toEntity(AppointmentRequestDTO dto);

    @Mapping(target = "professionalId", expression = "java(entity.getProfessionalId())")
    @Mapping(target = "employeeId", expression = "java(entity.getEmployeeId())")
    @Mapping(target = "patientId", expression = "java(entity.getPatientId())")
    @Mapping(target = "serviceTypeId", expression = "java(entity.getServiceTypeId())")
    @Mapping(target = "status", expression = "java(entity.getStatus().getLabel())")
    @Mapping(target = "priority", expression = "java(entity.getPriorit().getLabel())")
    public abstract AppointmentResponseDTO toDto(Appointment entity);

    public Appointment toEntityWhenHasId(Appointment entity, AppointmentRequestDTO dto) {
        entity.setStatus(Status.fromLabel(dto.getStatus()));
        entity.setPriorit(Priority.fromLabel(dto.getPriority()));
        entity.setNotes(dto.getNotes());
        entity.setScheduledAt(dto.getScheduledAt());
        return entity;
    }
}