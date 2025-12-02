package com.project.healthsystem.validator;

import com.project.healthsystem.model.Login;
import com.project.healthsystem.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class LoginValidator {
    @Autowired
    private LoginRepository loginRepository;

    public Login validate(String login){
        Login loginVerifyed = loginRepository.findByLogin(login)
            .orElseThrow(() -> new AccessDeniedException("Credenciais inválidas!"));
        return loginVerifyed;
    }
}
