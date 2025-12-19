package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.PersonRequestDTO;
import com.project.healthsystem.controller.dto.PersonResponseDTO;
import com.project.healthsystem.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PersonMapper {

    @Mapping(target = "cpf", expression = "java(dto.getCpfNormalized())")
    public abstract Person toEntity(PersonRequestDTO dto);

    @Mapping(target = "agentId", expression = "java(entity.getAgentId())")
    @Mapping(target = "conditionsId", expression = "java(entity.getConditionsId())")
    @Mapping(target = "responsibleId", expression = "java(entity.getResponsibleId())")
    public abstract PersonResponseDTO toDto(Person entity);

    public Person toEntityWhenHasId(Person entity, PersonRequestDTO dto){
        entity.setName(dto.getName());
        entity.setMotherName(dto.getMotherName());
        entity.setBirthday(dto.getBirthday());
        entity.setCns(dto.getCns());
        entity.setCpf(dto.getCpf());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        return entity;
    }
}
