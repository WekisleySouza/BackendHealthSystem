package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AgentRequestDTO;
import com.project.healthsystem.controller.dto.AgentResponseDTO;
import com.project.healthsystem.model.Agent;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-13T18:09:26-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class AgentMapperImpl extends AgentMapper {

    @Override
    public Agent toEntity(AgentRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Agent agent = new Agent();

        agent.setPerson( map(dto) );

        return agent;
    }

    @Override
    public AgentResponseDTO toDto(Agent agent) {
        if ( agent == null ) {
            return null;
        }

        AgentResponseDTO agentResponseDTO = new AgentResponseDTO();

        agentResponseDTO.setId( agent.getId() );

        agentResponseDTO.setCpf( agent.getPerson().getCpf() );
        agentResponseDTO.setName( agent.getPerson().getName() );
        agentResponseDTO.setPhone( agent.getPerson().getPhone() );
        agentResponseDTO.setBirthday( agent.getPerson().getBirthday() );
        agentResponseDTO.setEmail( agent.getPerson().getEmail() );

        return agentResponseDTO;
    }
}
