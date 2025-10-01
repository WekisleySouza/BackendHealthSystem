package com.project.healthsystem.service;

import com.project.healthsystem.model.Agent;
import com.project.healthsystem.repository.AgentRepository;
import com.project.healthsystem.validator.AgentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository repository;
    private final AgentValidator validator;

    public Agent save(Agent agent){
        validator.validate(agent);
        return this.repository.save(agent);
    }

    public void update(Agent agent){ repository.save(agent); }

    public Optional<Agent> findById(long id){ return repository.findById(id); }

    public List<Agent> getAll(){ return repository.findAll(); }

    public void delete(Agent agent){ repository.delete(agent); }

}
