package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ServiceTypeRequestDTO;
import com.project.healthsystem.controller.dto.ServiceTypeResponseDTO;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.model.ServiceTypes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ServiceTypeMapper {

    @Mapping(target = "type", expression = "java(com.project.healthsystem.model.ServiceTypes.fromLabel(dto.getType()))")
    public abstract ServiceType toEntity(ServiceTypeRequestDTO dto);

    @Mapping(target = "categoryGroupId", expression = "java(entity.getCategoryGroupId())")
    @Mapping(target = "type", expression = "java(entity.getType().getLabel())")
    public abstract ServiceTypeResponseDTO toDto(ServiceType entity);

    public ServiceType toEntityWhenHasId(ServiceType entity, ServiceTypeRequestDTO dto){
        entity.setType(ServiceTypes.fromLabel(dto.getType()));
        entity.setName(dto.getName());
        entity.setValue(dto.getValue());
        return entity;
    }
}