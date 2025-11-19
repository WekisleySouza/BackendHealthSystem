package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.AgentRequestDTO;
import com.project.healthsystem.controller.mappers.AgentMapper;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.repository.AgentRepository;
import com.project.healthsystem.validator.AgentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository repository;
    private final AgentValidator agentValidator;
    private final AgentMapper agentMapper;

    public Agent save(AgentRequestDTO agentRequestDTO){
        Agent agent = agentValidator.validateSave(agentRequestDTO);
        return this.repository.save(agent);
    }

    public void update(AgentRequestDTO agentRequestDTO, long id){
        Agent agent = agentValidator.validateUpdate(agentRequestDTO, id);
        this.repository.save(agent);
    }

    public AgentRequestDTO findById(long id){
        Agent agent = agentValidator.validateFindById(id);
        return agentMapper.toDto(agent);
    }

    public Page<AgentRequestDTO> getAll(Integer pageNumber, Integer pageLength){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository
            .findAll(pageRequest)
            .map(agentMapper::toDto);
    }

    public void delete(long id){
        Agent agent = agentValidator.validateDelete(id);
        repository.delete(agent);
    }
}
