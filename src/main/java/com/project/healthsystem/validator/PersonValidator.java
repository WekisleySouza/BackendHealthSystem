package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonValidator {
    private final PersonRepository personRepository;

    public Person validateFindByCpf(String cpf){
        return personRepository.findByCpf(cpf)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado nenhum usuário com este cpf!"));
    }

    public Person validateGetReferenceByCpf(String cpf){
        return personRepository.getReferenceByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("Não foi encontrado nenhum usuário com este cpf!"));
    }
}
