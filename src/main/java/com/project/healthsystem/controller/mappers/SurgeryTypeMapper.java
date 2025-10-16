package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.SurgeryTypeDTO;
import com.project.healthsystem.model.SurgeryType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class SurgeryTypeMapper {

    public abstract SurgeryType toEntity(SurgeryTypeDTO dto);

    public abstract SurgeryTypeDTO toDto(SurgeryType entity);

    public SurgeryType toEntityWhenHasId(SurgeryType entity, SurgeryTypeDTO dto){
        SurgeryType newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}
