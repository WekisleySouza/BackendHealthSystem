package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AppointmentResponseDTO;
import com.project.healthsystem.model.Agreements;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.model.Priority;
import com.project.healthsystem.model.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AppointmentsMapper {

    @Mapping(target = "status", expression = "java(com.project.healthsystem.model.Status.fromLabel(dto.getStatus()))")
    @Mapping(target = "agreements", expression = "java(com.project.healthsystem.model.Agreements.fromLabel(dto.getAgreements()))")
    @Mapping(target = "priorit", expression = "java(com.project.healthsystem.model.Priority.fromLabel(dto.getPriority()))")
    public abstract Appointment toEntity(AppointmentRequestDTO dto);

    @Mapping(target = "responsibleProfessionalName", expression = "java(entity.getResponsibleProfessionalName())")
    @Mapping(target = "requestingProfessionalName", expression = "java(entity.getRequestingProfessionalName())")
    @Mapping(target = "employeeName", expression = "java(entity.getEmployeeName())")
    @Mapping(target = "patientName", expression = "java(entity.getPatientName())")
    @Mapping(target = "serviceTypeName", expression = "java(entity.getServiceTypeName())")
    @Mapping(target = "serviceType", expression = "java(entity.getServiceType())")
    @Mapping(target = "categoryGroupName", expression = "java(entity.getServiceTypeCategoryGroupName())")
    @Mapping(target = "status", expression = "java(entity.getStatus().getLabel())")
    @Mapping(target = "agreements", expression = "java(entity.getAgreements().getLabel())")
    @Mapping(target = "priority", expression = "java(entity.getPriorit().getLabel())")
    public abstract AppointmentResponseDTO toDto(Appointment entity);

    public Appointment toEntityWhenHasId(Appointment entity, AppointmentRequestDTO dto) {
        entity.setStatus(Status.fromLabel(dto.getStatus()));
        entity.setAgreements(Agreements.fromLabel(dto.getAgreements()));
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setPriorit(Priority.fromLabel(dto.getPriority()));
        entity.setNotes(dto.getNotes());
        entity.setScheduledAt(dto.getScheduledAt());
        entity.setReturn(dto.isReturn());
        entity.setSchedulingForecast(dto.getSchedulingForecast());
        return entity;
    }
}