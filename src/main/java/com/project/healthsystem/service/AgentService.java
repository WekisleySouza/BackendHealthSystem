package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.AgentDTO;
import com.project.healthsystem.controller.mappers.AgentMapper;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.repository.AgentRepository;
import com.project.healthsystem.validator.AgentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository repository;
    private final AgentValidator agentValidator;
    private final AgentMapper agentMapper;

    public Agent save(AgentDTO agentDTO){
        Agent agent = agentValidator.validateSave(agentDTO);
        return this.repository.save(agent);
    }

    public void update(AgentDTO agentDTO, long id){
        Agent agent = agentValidator.validateUpdate(agentDTO, id);
        this.repository.save(agent);
    }

    public AgentDTO findById(long id){
        Agent agent = agentValidator.validateFindById(id);
        return agentMapper.toDto(agent);
    }

    public List<AgentDTO> getAll(){
        return repository
                .findAll()
                .stream()
                .map(agentMapper::toDto)
                .collect(Collectors.toList());
    }

    public void delete(long id){
        Agent agent = agentValidator.validateDelete(id);
        repository.delete(agent);
    }
}
