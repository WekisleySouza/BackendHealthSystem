package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.PatientRequestDTO;
import com.project.healthsystem.controller.dto.PatientResponseDTO;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PatientMapper {

    @Mapping(target = "person", expression = "java(map(dto))")
    public abstract Patient toEntity(PatientRequestDTO dto);

    @Mapping(target = "agentId", expression = "java(entity.getAgentId())")
    @Mapping(target = "conditionsId", expression = "java(entity.getConditionsId())")
    @Mapping(target = "responsibleId", expression = "java(entity.getResponsibleId())")
    @Mapping(target = "name", expression = "java(entity.getPerson().getName())")
    @Mapping(target = "birthday", expression = "java(entity.getPerson().getBirthday())")
    @Mapping(target = "cpf", expression = "java(entity.getPerson().getCpf())")
    @Mapping(target = "phone", expression = "java(entity.getPerson().getPhone())")
    @Mapping(target = "address", expression = "java(entity.getPerson().getAddress())")
    @Mapping(target = "email", expression = "java(entity.getPerson().getEmail())")
    public abstract PatientResponseDTO toDto(Patient entity);

    public Patient toEntityWhenHasId(Patient entity, PatientRequestDTO dto){
        entity.setMotherName(dto.getMotherName());
        entity.setCns(dto.getCns());
        entity.getPerson().setCpf(dto.getCpf());
        entity.getPerson().setName(dto.getName());
        entity.getPerson().setAddress(dto.getAddress());
        entity.getPerson().setPhone(dto.getPhone());
        entity.getPerson().setBirthday(dto.getBirthday());
        entity.getPerson().setEmail(dto.getEmail());
        return entity;
    }

    protected Person map(PatientRequestDTO dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setAddress(dto.getAddress());
        person.setPhone(dto.getPhone());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        return person;
    }
}
