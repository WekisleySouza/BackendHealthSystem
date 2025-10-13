package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AgentDTO;
import com.project.healthsystem.model.Agent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgentMapper {

    Agent toEntity(AgentDTO dto);

    AgentDTO toDto(Agent agent);
}
