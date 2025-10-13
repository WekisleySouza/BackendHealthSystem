package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.AgentDTO;
import com.project.healthsystem.controller.mappers.AgentMapper;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.repository.AgentRepository;
import com.project.healthsystem.validator.AgentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository repository;
    private final AgentValidator agentValidator;
    private final AgentMapper agentMapper;

    public Agent save(Agent agent){
        agentValidator.validate(agent);
        return this.repository.save(agent);
    }

    public void update(AgentDTO agentDTO, long id){
        agentValidator.validate(id);
        Optional<Agent> agentOptional = repository.findById(id);
        Agent agent = agentMapper.toEntity(agentDTO);


    }

//    public Optional<AgentDTO> findById(long id){
//        agentValidator.validate(id);
//        return agentMapper.toDto(repository.findById(id).get());
//    }

    public List<AgentDTO> getAll(){
        return repository
                .findAll()
                .stream()
                .map(AgentDTO::new)
                .collect(Collectors.toList());
    }

    public void delete(long id){
        agentValidator.validate(id);
        Optional<Agent> agentOptional = repository.findById(id);
        repository.delete(agentOptional.get());
    }

}
