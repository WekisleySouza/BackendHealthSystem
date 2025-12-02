package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.AgentRequestDTO;
import com.project.healthsystem.controller.dto.AgentResponseDTO;
import com.project.healthsystem.controller.mappers.AgentMapper;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.repository.AgentRepository;
import com.project.healthsystem.repository.specs.AgentSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.AgentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository repository;
    private final AgentValidator agentValidator;
    private final AgentMapper agentMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public Agent save(AgentRequestDTO agentRequestDTO, String token){
        Agent agent = agentValidator.validateSave(agentRequestDTO);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        agent.createdNow();
        agent.setCreatedBy(currentEditor);
        agent.setLastModifiedBy(currentEditor);
        return this.repository.save(agent);
    }

    public void update(AgentRequestDTO agentRequestDTO, long id, String token){
        Agent agent = agentValidator.validateUpdate(agentRequestDTO, id);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        agent.setLastModifiedBy(currentEditor);
        agent.updatedNow();
        this.repository.save(agent);
    }

    public AgentResponseDTO findById(long id){
        Agent agent = agentValidator.validateFindById(id);
        return agentMapper.toResponseDto(agent);
    }

    public Page<AgentResponseDTO> getAll(
        Integer pageNumber,
        Integer pageLength,
        String name,
        String cpf,
        String phone,
        LocalDate birthday,
        String email
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<Agent> specs =  null;
        specs = SpecsCommon.addSpec(specs, AgentSpecs.nameEqual(name));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.cpfEqual(cpf));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.phoneEqual(phone));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.birthdayEqual(birthday));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.emailEqual(email));
        return repository
            .findAll(specs, pageRequest)
            .map(agentMapper::toResponseDto);
    }

    public void delete(long id){
        Agent agent = agentValidator.validateDelete(id);
        repository.delete(agent);
    }
}
