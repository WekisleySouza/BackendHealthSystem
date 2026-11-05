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
    @Mapping(target = "priorit", expression = "java(com.project.healthsystem.model.Priority.fromLabel(dto.getPriority()))")
    @Mapping(target = "requestingInstituition", ignore = true)
    @Mapping(target = "regulatingInstituition", ignore = true)
    @Mapping(target = "authorInstituition", ignore = true)
    @Mapping(target = "executingInstituition", ignore = true)
    @Mapping(target = "careModality", expression = "java(com.project.healthsystem.model.CareModality.fromLabel(dto.getCareModality()))")
    @Mapping(target = "requestCharacter", expression = "java(com.project.healthsystem.model.RequestCharacter.fromLabel(dto.getRequestCharacter()))")
    public abstract Appointment toEntity(AppointmentRequestDTO dto);

    @Mapping(target = "responsibleProfessionalName", expression = "java(entity.getResponsibleProfessionalName())")
    @Mapping(target = "requestingProfessionalName", expression = "java(entity.getRequestingProfessionalName())")
    @Mapping(target = "employeeName", expression = "java(entity.getEmployeeName())")
    @Mapping(target = "instituitionName", expression = "java(entity.getInstituitionName())")
    @Mapping(target = "patientName", expression = "java(entity.getPatientName())")
    @Mapping(target = "serviceTypeName", expression = "java(entity.getServiceTypeName())")
    @Mapping(target = "serviceType", expression = "java(entity.getServiceType().getType().getLabel())")
    @Mapping(target = "categoryGroupName", expression = "java(entity.getServiceTypeCategoryGroupName())")
    @Mapping(target = "status", expression = "java(entity.getStatus().getLabel())")
    @Mapping(target = "agreementName", expression = "java(entity.getAgreementName())")
    @Mapping(target = "priority", expression = "java(entity.getPriorit().getLabel())")
    @Mapping(target = "requestingInstituitionId", source = "requestingInstituition.id")
    @Mapping(target = "requestingInstituitionName", source = "requestingInstituition.name")
    @Mapping(target = "regulatingInstituitionId", source = "regulatingInstituition.id")
    @Mapping(target = "regulatingInstituitionName", source = "regulatingInstituition.name")
    @Mapping(target = "authorInstituitionId", source = "authorInstituition.id")
    @Mapping(target = "authorInstituitionName", source = "authorInstituition.name")
    @Mapping(target = "executingInstituitionId", source = "executingInstituition.id")
    @Mapping(target = "executingInstituitionName", source = "executingInstituition.name")
    @Mapping(target = "careModality", expression = "java(entity.getCareModality() == null ? null : entity.getCareModality().getLabel())")
    @Mapping(target = "requestCharacter", expression = "java(entity.getRequestCharacter() == null ? null : entity.getRequestCharacter().getLabel())")
    public abstract AppointmentResponseDTO toDto(Appointment entity);

    public Appointment toEntityWhenHasId(Appointment entity, AppointmentRequestDTO dto) {
        entity.setStatus(Status.fromLabel(dto.getStatus()));
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setPriorit(Priority.fromLabel(dto.getPriority()));
        entity.setNotes(dto.getNotes());
        entity.setScheduledAt(dto.getScheduledAt());
        entity.setReturn(dto.isReturn());
        entity.setSchedulingForecast(dto.getSchedulingForecast());
        if (dto.getCareModality() != null) entity.setCareModality(com.project.healthsystem.model.CareModality.fromLabel(dto.getCareModality()));
        if (dto.getRequestCharacter() != null) entity.setRequestCharacter(com.project.healthsystem.model.RequestCharacter.fromLabel(dto.getRequestCharacter()));
        if (dto.getClinicalReasonSystem() != null) entity.setClinicalReasonSystem(dto.getClinicalReasonSystem());
        if (dto.getClinicalReasonCode() != null) entity.setClinicalReasonCode(dto.getClinicalReasonCode());
        if (dto.getRequestedAt() != null) entity.setRequestedAt(dto.getRequestedAt());
        if (dto.getExecutingCbo() != null) entity.setExecutingCbo(dto.getExecutingCbo());
        return entity;
    }
}