package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.basic_requests.LoginRequestDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.LoginRepository;
import com.project.healthsystem.repository.PasswordResetTokenRepository;
import com.project.healthsystem.validator.LoginValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository repository;
    private final RoleService roleService;
    private final LoginValidator loginValidator;
    private final PasswordEncoder passwordEncoder;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    @Value("${app.default-admin.username}")
    private String DEFAULT_ADMIN_USERNAME;
    @Value("${app.default-admin.password}")
    private String DEFAULT_ADMIN_PASSWORD;
    @Value("${app.default-employee-password}")
    private String DEFAULT_EMPLOYEE_PASSWORD;
    @Value("${app.default-user-password}")
    private String DEFAULT_USER_PASSWORD;

    public void createDefaultAdmin(Person person){
        if(!repository.existsByLogin(person.getCpf())) {
            Login login = new Login();
            login.setLogin(DEFAULT_ADMIN_USERNAME);
            login.setPassword(this.passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));

            Role role = roleService.findByRole(Roles.ADMIN);
            person.getRoles().add(role);

            login.setPerson(person);
            repository.save(login);
        }
    }

    public void createDefaultLoginTo(Employee employee){
        if(!(employee.getPerson().getCpf().isEmpty() || repository.existsByLogin(employee.getPerson().getCpf()))){
            Login login = new Login();
            login.setLogin(employee.getPerson().getCpf());
            login.setPassword(this.passwordEncoder.encode(this.DEFAULT_EMPLOYEE_PASSWORD));
            login.setPerson(employee.getPerson());

            repository.save(login);
        }
    }

    public void createDefaultLoginTo(Patient patient){
        if(!(patient.getPerson().getCpf().isEmpty() || repository.existsByLogin(patient.getPerson().getCpf()))) {
            String cpf = patient.getPerson().getCpf();

            if(cpf != null && !cpf.isEmpty()){
                Login login = new Login();

                login.setLogin(cpf);
                login.setPassword(this.passwordEncoder.encode(this.DEFAULT_USER_PASSWORD));
                login.setPerson(patient.getPerson());

                repository.save(login);
            }
        }
    }

    public void updatePassword(Login login, LoginRequestDTO loginRequestDTO){
        login.setPassword(passwordEncoder.encode(loginRequestDTO.getPassword()));
        repository.save(login);
    }

    public void updateLogin(long personId, String newLogin){
        Login login = this.repository.findByPersonId(personId)
            .orElseThrow(() -> new NotFoundException("Login não existe!"));
        login.setLogin(newLogin);
        repository.save(login);
    }

    public Login findByLogin(String login){
        return this.loginValidator.validate(login);
    }

    public void delete(long id){
        Login login = repository.getReferenceById(id);
        repository.delete(login);
    }

    @Transactional
    public void forgotPassword(String email){

        Login login = this.loginValidator.validateFindByEmail(email);
        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setLogin(login);
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        this.passwordResetTokenRepository.save(passwordResetToken);

        emailService.sendResetPasswordEmail(
            login.getPerson().getEmail(),
            token
        );
    }

    public void resetPassword(String token, String newPassword){
        PasswordResetToken passwordResetToken = loginValidator.validateFindPasswordResetTokenByToken(token);

        Login login = passwordResetToken.getLogin();
        login.setPassword(passwordEncoder.encode(newPassword));

        this.repository.save(login);

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public boolean hasLogin(Person person){
        return repository.existsByPersonId(person.getId());
    }
}
