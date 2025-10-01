package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.DuplicatedRegisterException;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AgentValidator {

    @Autowired
    private AgentRepository repository;

    public void validate(Agent agent){
        if(isDuplicatedAgent(agent)){
            throw new DuplicatedRegisterException("Agent already exists!");
        }
    }

    private boolean isDuplicatedAgent(Agent agent){
        return true;
    }
}
