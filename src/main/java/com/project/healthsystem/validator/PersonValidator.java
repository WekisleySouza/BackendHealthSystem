package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonValidator {

    @Autowired
    private PersonRepository repository;

    public void validate(Person person){

    }

    public void validate(long id){
        if(!exists(id)){
            throw new NotFoundException("Não existe pessoa com este id!");
        }
    }

    private boolean exists(long id){
        return repository.existsById(id);
    }

}
