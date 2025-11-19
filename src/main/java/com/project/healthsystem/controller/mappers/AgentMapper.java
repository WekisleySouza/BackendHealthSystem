package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AgentRequestDTO;
import com.project.healthsystem.model.Agent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AgentMapper {

    public abstract Agent toEntity(AgentRequestDTO dto);

    public abstract AgentRequestDTO toDto(Agent agent);

    public Agent toEntityWhenHasId(Agent agent, AgentRequestDTO agentRequestDTO){
        Agent newAgent = toEntity(agentRequestDTO);
        agent.setId(agent.getId());
        return newAgent;
    }
}
