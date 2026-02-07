package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AgentRequestDTO;
import com.project.healthsystem.controller.dto.AgentResponseDTO;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Gender;
import com.project.healthsystem.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AgentMapper {

    @Mapping(target = "person", expression = "java(map(dto))")
    public abstract Agent toEntity(AgentRequestDTO dto);

    @Mapping(target = "cpf", expression = "java(agent.getPerson().getCpf())")
    @Mapping(target = "name", expression = "java(agent.getPerson().getName())")
    @Mapping(target = "gender", expression = "java(agent.getPerson().getGender().getLabel())")
    @Mapping(target = "phone", expression = "java(agent.getPerson().getPhone())")
    @Mapping(target = "birthday", expression = "java(agent.getPerson().getBirthday())")
    @Mapping(target = "address", expression = "java(agent.getPerson().getAddress())")
    @Mapping(target = "email", expression = "java(agent.getPerson().getEmail())")
    public abstract AgentResponseDTO toDto(Agent agent);

    public Agent toEntityWhenHasId(Agent entity, AgentRequestDTO dto){
        entity.getPerson().setCpf(dto.getCpf());
        entity.getPerson().setName(dto.getName());
        entity.getPerson().setGender(Gender.fromLabel(dto.getGender()));
        entity.getPerson().setAddress(dto.getAddress());
        entity.getPerson().setPhone(dto.getPhone());
        entity.getPerson().setBirthday(dto.getBirthday());
        entity.getPerson().setEmail(dto.getEmail());
        return entity;
    }

    protected Person map(AgentRequestDTO dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setGender(Gender.fromLabel(dto.getGender()));
        person.setCpf(dto.getCpfNormalized());
        person.setAddress(dto.getAddress());
        person.setPhone(dto.getPhone());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        return person;
    }
}
