package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.PersonDTO;
import com.project.healthsystem.controller.mappers.PersonMapper;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.PersonRepository;
import com.project.healthsystem.validator.PersonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository repository;
    private final PersonValidator personValidator;
    private final PersonMapper personMapper;

    public Person save(PersonDTO personDTO){
        Person person = personValidator.validateSave(personDTO);
        return repository.save(person);
    }

    public void update(PersonDTO personDTO, long id){
        Person person = personValidator.validateUpdate(personDTO, id);
        repository.save(person);
    }

    public PersonDTO findById(long id){
        return personMapper.toDto(personValidator.validateFindById(id));
    }

    public List<PersonDTO> search(){
        List<Person> persons = repository.findAll();
        return persons.stream()
            .map(personMapper::toDto)
            .collect(Collectors.toList());
    }

    public void delete(long id){
        repository.delete(personValidator.validateDelete(id));
    }
}
