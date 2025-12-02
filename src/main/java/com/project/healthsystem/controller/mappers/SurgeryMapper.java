package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.SurgeryRequestDTO;
import com.project.healthsystem.model.Surgery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class SurgeryMapper {

    public abstract Surgery toEntity(SurgeryRequestDTO dto);

    @Mapping(target = "surgeryTypeId", expression = "java(entity.getSurgeryTypeId())")
    public abstract SurgeryRequestDTO toDto(Surgery entity);

    public Surgery toEntityWhenHasId(Surgery entity, SurgeryRequestDTO dto){
        entity.setDateTime(dto.getDateTime());
        entity.setPersonName(dto.getPersonName());
        entity.setSurgeryRisk(dto.getSurgeryRisk());
        entity.setLocation(dto.getLocation());
        entity.setConclusion(dto.getConclusion());
        entity.setSusEasy(dto.getSusEasy());
        entity.setSesap(dto.getSesap());
        entity.setProcedureDate(dto.getProcedureDate());
        entity.setAnesthesicRisk(dto.getAnesthesicRisk());
        entity.setObservation(dto.getObservation());
        return entity;
    }
}
