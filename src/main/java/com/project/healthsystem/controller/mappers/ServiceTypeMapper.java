package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ServiceTypeRequestDTO;
import com.project.healthsystem.model.ServiceType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ServiceTypeMapper {

    public abstract ServiceType toEntity(ServiceTypeRequestDTO dto);

    @Mapping(target = "categoryGroupId", expression = "java(entity.getCategoryGroupId())")
    public abstract ServiceTypeRequestDTO toDto(ServiceType entity);

    public ServiceType toEntityWhenHasId(ServiceType entity, ServiceTypeRequestDTO dto){
        ServiceType newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}