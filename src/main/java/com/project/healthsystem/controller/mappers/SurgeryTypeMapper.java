package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.SurgeryTypeRequestDTO;
import com.project.healthsystem.model.SurgeryType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class SurgeryTypeMapper {

    public abstract SurgeryType toEntity(SurgeryTypeRequestDTO dto);

    public abstract SurgeryTypeRequestDTO toDto(SurgeryType entity);

    public SurgeryType toEntityWhenHasId(SurgeryType entity, SurgeryTypeRequestDTO dto){
        SurgeryType newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}
