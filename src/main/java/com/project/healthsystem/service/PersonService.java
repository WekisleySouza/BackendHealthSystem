package com.project.healthsystem.service;

import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.PersonRepository;
import com.project.healthsystem.validator.PersonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonValidator personValidator;

    public Person save(Person person){
        return personRepository.save(person);
    }

    public Person findByCpf(String cpf){
        return personValidator.validateFindByCpf(cpf);
    }

    public Person getReferenceByCpf(String cpf){
        return personValidator.validateGetReferenceByCpf(cpf);
    }

    public boolean existsPersonByCpf(String cpf){
        return personRepository.existsByCpf(cpf);
    }
}
