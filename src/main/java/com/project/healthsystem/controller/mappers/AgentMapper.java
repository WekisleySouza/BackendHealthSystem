package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AgentRequestDTO;
import com.project.healthsystem.controller.dto.AgentResponseDTO;
import com.project.healthsystem.model.Agent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AgentMapper {

    @Mapping(target = "cpf", expression = "java(dto.getCpfNormalized())")
    public abstract Agent toEntity(AgentRequestDTO dto);

    public abstract AgentResponseDTO toDto(Agent agent);

    public Agent toEntityWhenHasId(Agent agent, AgentRequestDTO agentRequestDTO){
        agent.setCpf(agentRequestDTO.getCpf());
        agent.setName(agentRequestDTO.getName());
        agent.setPhone(agentRequestDTO.getPhone());
        agent.setBirthday(agentRequestDTO.getBirthday());
        agent.setEmail(agentRequestDTO.getEmail());
        return agent;
    }
}
