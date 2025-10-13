package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.PersonDTO;
import com.project.healthsystem.controller.mappers.PersonMapper;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.AgentRepository;
import com.project.healthsystem.repository.PersonRepository;
import com.project.healthsystem.validator.PersonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository repository;
    private final AgentRepository agentRepository;
    private final ConditionService conditionService;
    private final PersonValidator personValidator;
    private final PersonMapper personMapper;

    public Person save(PersonDTO personDTO){
        Optional<Agent> agent = agentRepository.findById(personDTO.getAgentId());
        List<Condition> conditions = conditionService.findByIds(personDTO.getConditionsId());

        Person person = personMapper.toEntity(personDTO);

        if(conditions.size() > 0){
            person.setConditions(conditions);
        }

        if(agent.isPresent()){
            person.setAgent(agent.get());
        }

        return repository.save(person);
    }

    public void update(PersonDTO personDTO, long id){
        personValidator.validate(id);

        Optional<Person> personOptional = repository.findById(id);
        Optional<Agent> agent = agentRepository.findById(personDTO.getAgentId());
        List<Condition> conditions = conditionService.findByIds(personDTO.getConditionsId());

        var person = personMapper.toEntityWhenHasId(personOptional.get(), personDTO);
        personValidator.validate(person);
        person.setConditions(conditions);

        if(agent.isPresent()){
            person.setAgent(agent.get());
        }

        repository.save(person);
    }

    public PersonDTO findById(long id){
        personValidator.validate(id);
        return personMapper.toDto(repository.findById(id).get());
    }

    public List<PersonDTO> search(){
        List<Person> persons = repository.findAll();
        return persons.stream()
            .map(personMapper::toDto)
            .collect(Collectors.toList());
    }

    public void delete(long id){
        personValidator.validate(id);
        repository.delete(repository.findById(id).get());
    }
}
