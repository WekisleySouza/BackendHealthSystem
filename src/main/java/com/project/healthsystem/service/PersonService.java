package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.PersonDTO;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.AgentRepository;
import com.project.healthsystem.repository.PersonRepository;
import com.project.healthsystem.validator.PersonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    public Person save(PersonDTO personDTO){
        Optional<Agent> agent = agentRepository.findById(personDTO.getAgentId());
        List<Condition> conditions = conditionService.findByIds(personDTO.getConditionsId());

        Person person = personDTO.mappingToPerson();

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

        var person = personOptional.get();

        personValidator.validate(person);

        person.coppingFromPersonDto(personDTO);
        person.setConditions(conditions);

        if(agent.isPresent()){
            person.setAgent(agent.get());
        }

        repository.save(person);
    }

    public PersonDTO findById(long id){
        personValidator.validate(id);

        Optional<Person> personOptional = repository.findById(id);

        if(personOptional.isPresent()){
            Person person = personOptional.get();
            PersonDTO personDTO = new PersonDTO(
                    person.getName(),
                    person.getBirthday(),
                    person.getCns(),
                    person.getCpf()
            );
            return personDTO;
        }

        return null;
    }

    public List<PersonDTO> search(){
        List<Person> persons = repository.findAll();
        return persons.stream()
            .map(PersonDTO::new)
            .collect(Collectors.toList());
    }

    public void delete(long id){
        personValidator.validate(id);

        Optional<Person> personOptional = repository.findById(id);

        if(personOptional.isPresent()){
            repository.delete(personOptional.get());
        }
    }
}
