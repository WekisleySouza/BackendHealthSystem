package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AgentResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.AgentRequestDTO;
import com.project.healthsystem.model.Agent;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T18:57:03-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
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
        agentResponseDTO.setGender( agent.getPerson().getGender().getLabel() );
        agentResponseDTO.setSex( agent.getPerson().getSex().getLabel() );
        agentResponseDTO.setCellPhone( agent.getPerson().getCellPhone() );
        agentResponseDTO.setResidentialPhone( agent.getPerson().getResidentialPhone() );
        agentResponseDTO.setContactPhone( agent.getPerson().getContactPhone() );
        agentResponseDTO.setBirthday( agent.getPerson().getBirthday() );
        agentResponseDTO.setAddress( agent.getPerson().getAddress() );
        agentResponseDTO.setEmail( agent.getPerson().getEmail() );

        return agentResponseDTO;
    }
}
