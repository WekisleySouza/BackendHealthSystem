package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ProfessionalRequestDTO;
import com.project.healthsystem.controller.dto.ProfessionalResponseDTO;
import com.project.healthsystem.model.Gender;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Professional;
import com.project.healthsystem.model.Sex;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ProfessionalMapper {

    @Mapping(target = "person", expression = "java(map(dto))")
    public abstract Professional toEntity(ProfessionalRequestDTO dto);

    @Mapping(target = "cpf", expression = "java(entity.getPerson().getCpf())")
    @Mapping(target = "name", expression = "java(entity.getPerson().getName())")
    @Mapping(target = "gender", expression = "java(entity.getPerson().getGender().getLabel())")
    @Mapping(target = "sex", expression = "java(entity.getPerson().getSex().getLabel())")
    @Mapping(target = "cellPhone", expression = "java(entity.getPerson().getCellPhone())")
    @Mapping(target = "residentialPhone", expression = "java(entity.getPerson().getResidentialPhone())")
    @Mapping(target = "contactPhone", expression = "java(entity.getPerson().getContactPhone())")
    @Mapping(target = "birthday", expression = "java(entity.getPerson().getBirthday())")
    @Mapping(target = "address", expression = "java(entity.getPerson().getAddress())")
    @Mapping(target = "email", expression = "java(entity.getPerson().getEmail())")
    public abstract ProfessionalResponseDTO toDto(Professional entity);

    public Professional toEntityWhenHasId(Professional entity, ProfessionalRequestDTO dto){

        entity.getPerson().setCpf(dto.getCpf());
        entity.getPerson().setName(dto.getName());
        entity.getPerson().setGender(Gender.fromLabel(dto.getGender()));
        entity.getPerson().setSex(Sex.fromLabel(dto.getSex()));
        entity.getPerson().setCellPhone(dto.getCellPhone());
        entity.getPerson().setResidentialPhone(dto.getResidentialPhone());
        entity.getPerson().setContactPhone(dto.getContactPhone());
        entity.getPerson().setAddress(dto.getAddress());
        entity.getPerson().setBirthday(dto.getBirthday());
        entity.getPerson().setEmail(dto.getEmail());
        return entity;
    }

    protected Person map(ProfessionalRequestDTO dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setGender(Gender.fromLabel(dto.getGender()));
        person.setCpf(dto.getCpfNormalized());
        person.setAddress(dto.getAddress());
        person.setAddress(dto.getAddress());
        person.setSex(Sex.fromLabel(dto.getSex()));
        person.setCellPhone(dto.getCellPhone());
        person.setResidentialPhone(dto.getResidentialPhone());
        person.setContactPhone(dto.getContactPhone());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        return person;
    }
}