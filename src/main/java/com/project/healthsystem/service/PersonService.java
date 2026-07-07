package com.project.healthsystem.service;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    public Person save(Person person){
        return personRepository.save(person);
    }

    public Person findById(long id){
        return personRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));
    }

    public Person findByCpf(String cpf){
        return personRepository.findByCpf(cpf)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado nenhum usuário com este cpf!"));
    }

    public Person getReferenceByCpf(String cpf){
        return personRepository.getReferenceByCpf(cpf)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado nenhum usuário com este cpf!"));
    }

    public boolean existsPersonByCpf(String cpf){
        return personRepository.existsByCpf(cpf);
    }
}
