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
        Surgery newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}
