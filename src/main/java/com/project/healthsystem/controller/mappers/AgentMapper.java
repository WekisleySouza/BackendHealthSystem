package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AgentRequestDTO;
import com.project.healthsystem.controller.dto.AgentResponseDTO;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AgentMapper {

    @Mapping(target = "person", expression = "java(map(dto))")
    public abstract Agent toEntity(AgentRequestDTO dto);

    @Mapping(target = "cpf", expression = "java(agent.getPerson().getCpf())")
    @Mapping(target = "name", expression = "java(agent.getPerson().getName())")
    @Mapping(target = "phone", expression = "java(agent.getPerson().getPhone())")
    @Mapping(target = "birthday", expression = "java(agent.getPerson().getBirthday())")
    @Mapping(target = "email", expression = "java(agent.getPerson().getEmail())")
    public abstract AgentResponseDTO toDto(Agent agent);

    public Agent toEntityWhenHasId(Agent agent, AgentRequestDTO agentRequestDTO){
        agent.getPerson().setCpf(agentRequestDTO.getCpf());
        agent.getPerson().setName(agentRequestDTO.getName());
        agent.getPerson().setPhone(agentRequestDTO.getPhone());
        agent.getPerson().setBirthday(agentRequestDTO.getBirthday());
        agent.getPerson().setEmail(agentRequestDTO.getEmail());
        return agent;
    }

    protected Person map(AgentRequestDTO dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setPhone(dto.getPhone());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        return person;
    }
}
