package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.PersonRequestDTO;
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

    public Person validateSave(PersonRequestDTO personRequestDTO){
        Agent agent = agentRepository.findById(personRequestDTO.getAgentId())
            .orElseThrow(() -> new InvalidDataException("Agent inválido!"));

        Person person = personMapper.toEntity(personRequestDTO);
        person.setAgent(agent);

        if(personRequestDTO.getResponsibleId() != null){
            Person responsible = personRepository.findById(personRequestDTO.getResponsibleId())
                .orElse(null);
            person.setResponsible(responsible);
        }
        if(personRequestDTO.getConditionsId() != null && !personRequestDTO.getConditionsId().isEmpty()){
            List<Condition> conditions = conditionService.findByIds(personRequestDTO.getConditionsId());
            person.setConditions(conditions);
        }

        return person;
    }

    public Person validateUpdate(PersonRequestDTO personRequestDTO, long id){
        Person person = personRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Person não encontrada!"));
        Agent agent = agentRepository.findById(personRequestDTO.getAgentId())
                .orElseThrow(() -> new InvalidDataException("Agent inválido!"));

        person = personMapper.toEntityWhenHasId(person, personRequestDTO);
        person.setAgent(agent);

        if(personRequestDTO.getResponsibleId() != null){
            Person responsible = personRepository.findById(personRequestDTO.getResponsibleId())
                    .orElse(null);
            person.setResponsible(responsible);
        }
        if(personRequestDTO.getConditionsId() != null && !personRequestDTO.getConditionsId().isEmpty()){
            List<Condition> conditions = conditionService.findByIds(personRequestDTO.getConditionsId());
            person.setConditions(conditions);
        }

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
