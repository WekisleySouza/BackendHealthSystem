package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ProfessionalRequestDTO;
import com.project.healthsystem.controller.dto.ProfessionalResponseDTO;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Professional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ProfessionalMapper {

    @Mapping(target = "person", expression = "java(map(dto))")
    public abstract Professional toEntity(ProfessionalRequestDTO dto);

    @Mapping(target = "cpf", expression = "java(entity.getPerson().getCpf())")
    @Mapping(target = "name", expression = "java(entity.getPerson().getName())")
    @Mapping(target = "phone", expression = "java(entity.getPerson().getPhone())")
    @Mapping(target = "birthday", expression = "java(entity.getPerson().getBirthday())")
    @Mapping(target = "email", expression = "java(entity.getPerson().getEmail())")
    public abstract ProfessionalResponseDTO toDto(Professional entity);

    public Professional toEntityWhenHasId(Professional entity, ProfessionalRequestDTO dto){
        entity.getPerson().setCpf(dto.getCpf());
        entity.getPerson().setName(dto.getName());
        entity.getPerson().setPhone(dto.getPhone());
        entity.getPerson().setBirthday(dto.getBirthday());
        entity.getPerson().setEmail(dto.getEmail());
        return entity;
    }

    protected Person map(ProfessionalRequestDTO dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setPhone(dto.getPhone());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        return person;
    }
}