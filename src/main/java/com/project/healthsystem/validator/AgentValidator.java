package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.AgentDTO;
import com.project.healthsystem.controller.mappers.AgentMapper;
import com.project.healthsystem.exceptions.DuplicatedRegisterException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AgentValidator {

    @Autowired
    private AgentRepository repository;
    private final AgentMapper agentMapper;

    public Agent validateSave(AgentDTO agentDTO){
        return agentMapper.toEntity(agentDTO);
    }

    public Agent validateUpdate(AgentDTO agentDTO, long id){
        Agent agent = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado um agent com este id!"));
        return agentMapper.toEntityWhenHasId(agent, agentDTO);
    }

    public Agent validateFindById(long id){
        Optional<Agent> agentOptional = repository.findById(id);
        if(agentOptional.isEmpty()){
            throw new NotFoundException("Não foi encontrado um agent com este id!");
        }
        return agentOptional.get();
    }

    public Agent validateDelete(long id){
        return this.validateFindById(id);
    }
}
