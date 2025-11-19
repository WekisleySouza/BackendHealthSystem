package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.PersonRequestDTO;
import com.project.healthsystem.controller.mappers.PersonMapper;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.PersonRepository;
import com.project.healthsystem.validator.PersonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository repository;
    private final LoginService loginService;
    private final PersonValidator personValidator;
    private final PersonMapper personMapper;

    public Person save(PersonRequestDTO personRequestDTO){
        Person person = personValidator.validateSave(personRequestDTO);
        person = repository.save(person);
        loginService.createDefaultLoginTo(person);
        return person;
    }

    public void update(PersonRequestDTO personRequestDTO, long id){
        Person person = personValidator.validateUpdate(personRequestDTO, id);
        repository.save(person);
    }

    public PersonRequestDTO findById(long id){
        return personMapper.toDto(personValidator.validateFindById(id));
    }

    public Page<PersonRequestDTO> search(Integer pageNumber, Integer pageLength){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Page<Person> persons = repository.findAll(pageRequest);
        return persons.map(personMapper::toDto);
    }

    public void delete(long id){
        repository.delete(personValidator.validateDelete(id));
    }
}
