package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.PersonRequestDTO;
import com.project.healthsystem.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PersonMapper {

    public abstract Person toEntity(PersonRequestDTO dto);

    @Mapping(target = "agentId", expression = "java(entity.getAgentId())")
    @Mapping(target = "conditionsId", expression = "java(entity.getConditionsId())")
    @Mapping(target = "responsibleId", expression = "java(entity.getResponsibleId())")
    public abstract PersonRequestDTO toDto(Person entity);

    public Person toEntityWhenHasId(Person entity, PersonRequestDTO dto){
        Person newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}
