package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginValidator {
    @Autowired
    private LoginRepository loginRepository;

    public void validate(Login login){

    }

    public void validate(long id){
        if(exists(id)){
            throw new NotFoundException("Não foi encontrado o login com este id!");
        }
    }

    private boolean exists(long id){
        return loginRepository.existsById(id);
    }
}
