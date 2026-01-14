package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.LoginRequestDTO;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.LoginRepository;
import com.project.healthsystem.validator.LoginValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository repository;
    private final RoleService roleService;
    private final LoginValidator loginValidator;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.username}")
    private String DEFAULT_ADMIN_USERNAME;
    @Value("${app.default-admin.password}")
    private String DEFAULT_ADMIN_PASSWORD;
    @Value("${app.default-employee-password}")
    private String DEFAULT_EMPLOYEE_PASSWORD;
    @Value("${app.default-user-password}")
    private String DEFAULT_USER_PASSWORD;

    public void createDefaultAdmin(Person person){
        Login login = new Login();
        login.setLogin(DEFAULT_ADMIN_USERNAME);
        login.setPassword(this.passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));

        Role role = roleService.findByRole(Roles.ADMIN);
        person.getRoles().add(role);

        login.setPerson(person);
        repository.save(login);
    }

    public void createDefaultLoginTo(Employee employee){
        Login login = new Login();
        login.setLogin(employee.getPerson().getCpf());
        login.setPassword(this.passwordEncoder.encode(this.DEFAULT_EMPLOYEE_PASSWORD));
        login.setPerson(employee.getPerson());

        repository.save(login);
    }

    public void createDefaultLoginTo(Patient patient){
        Login login = new Login();
        login.setLogin(patient.getPerson().getCpf());
        login.setPassword(this.passwordEncoder.encode(this.DEFAULT_USER_PASSWORD));
        login.setPerson(patient.getPerson());

        repository.save(login);
    }

    public void updatePassword(Login login, LoginRequestDTO loginRequestDTO){
        login.setPassword(passwordEncoder.encode(loginRequestDTO.getPassword()));
        repository.save(login);
    }

    public Login findByLogin(String login){
        return this.loginValidator.validate(login);
    }

    public void delete(long id){
        Login login = repository.getReferenceById(id);
        repository.delete(login);
    }
}
