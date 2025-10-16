package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.StatusDTO;
import com.project.healthsystem.model.Status;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class StatusMapper {

    public abstract Status toEntity(StatusDTO dto);

    public abstract StatusDTO toDto(Status entity);

    public Status toEntityWhenHasId(Status entity, StatusDTO dto){
        Status newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}