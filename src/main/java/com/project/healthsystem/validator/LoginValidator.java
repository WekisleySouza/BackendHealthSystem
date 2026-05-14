package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.PasswordResetToken;
import com.project.healthsystem.repository.LoginRepository;
import com.project.healthsystem.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginValidator {
    private final LoginRepository loginRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public Login validate(String login){
        Login loginVerifyed = loginRepository.findByLogin(login)
            .orElseThrow(() -> new AccessDeniedException("Credenciais inválidas!"));
        return loginVerifyed;
    }

    public Login validateFindByEmail(String email){
        Login loginVerifyed = loginRepository.findByPersonEmail(email)
            .orElseThrow(() -> new NotFoundException("Há algo errado com a sua requisição!"));
        return loginVerifyed;
    }

    public PasswordResetToken validateFindPasswordResetTokenByToken(String token){
        PasswordResetToken passwordResetTokenVerifyed = passwordResetTokenRepository.findByToken(token)
            .orElseThrow(() -> new NotFoundException("Token inválido!"));
        return passwordResetTokenVerifyed;
    }
}
