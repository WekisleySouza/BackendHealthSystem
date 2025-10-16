package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.PersonDTO;
import com.project.healthsystem.controller.mappers.PersonMapper;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.AgentRepository;
import com.project.healthsystem.repository.PersonRepository;
import com.project.healthsystem.service.ConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PersonValidator {

    private final PersonRepository personRepository;
    private final AgentRepository agentRepository;
    private final ConditionService conditionService;

    private final PersonMapper personMapper;

    public Person validateSave(PersonDTO personDTO){
        Agent agent = agentRepository.findById(personDTO.getAgentId())
            .orElseThrow(() -> new InvalidDataException("Agent inválido!"));
        Person responsible = personRepository.findById(personDTO.getResponsibleId())
            .orElse(null);
        List<Condition> conditions = conditionService.findByIds(personDTO.getConditionsId());

        Person person = personMapper.toEntity(personDTO);
        person.setAgent(agent);
        person.setResponsible(responsible);

        if(!conditions.isEmpty()){
            person.setConditions(conditions);
        }

        return person;
    }

    public Person validateUpdate(PersonDTO personDTO, long id){
        Person person = personRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Person não encontrada!"));
        Person responsible = personRepository.findById(personDTO.getResponsibleId())
            .orElse(null);
        Agent agent = agentRepository.findById(personDTO.getAgentId())
                .orElseThrow(() -> new InvalidDataException("Agent inválido!"));
        List<Condition> conditions = conditionService.findByIds(personDTO.getConditionsId());

        person = personMapper.toEntityWhenHasId(person, personDTO);
        person.setAgent(agent);
        person.setResponsible(responsible);
        person.setConditions(conditions);

        return person;
    }

    public Person validateFindById(long id){
        return personRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Person não encontrada!"));
    }

    public Person validateDelete(long id){
        return personRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Person não encontrada!"));
    }
}
