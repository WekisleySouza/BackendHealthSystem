package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.SurgeryDTO;
import com.project.healthsystem.model.Surgery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class SurgeryMapper {

    public abstract Surgery toEntity(SurgeryDTO dto);

    @Mapping(target = "surgeryTypeId", expression = "java(entity.getSurgeryTypeId())")
    public abstract SurgeryDTO toDto(Surgery entity);

    public Surgery toEntityWhenHasId(Surgery entity, SurgeryDTO dto){
        Surgery newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}
