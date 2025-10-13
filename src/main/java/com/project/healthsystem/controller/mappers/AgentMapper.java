package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AgentDTO;
import com.project.healthsystem.model.Agent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AgentMapper {

    public abstract Agent toEntity(AgentDTO dto);

    public abstract AgentDTO toDto(Agent agent);

    public Agent toEntityWhenHasId(Agent agent, AgentDTO agentDTO){
        Agent newAgent = toEntity(agentDTO);
        agent.setId(agent.getId());
        return newAgent;
    }
}
