package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.LoginRequestDTO;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Roles;
import com.project.healthsystem.repository.LoginRepository;
import com.project.healthsystem.validator.LoginValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository repository;
    private final LoginValidator loginValidator;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    public void createDefaultAdmin(Employee employee){
        Login login = new Login();
        login.setLogin(this.environment.getProperty("app.default-admin.username"));
        login.setPassword(this.passwordEncoder.encode(this.environment.getProperty("app.default-admin.password")));
        login.setRole(Roles.ADMIN);
        login.setEmployee(employee);
        repository.save(login);
    }

    public void createDefaultLoginTo(Employee employee, Roles role){
        Login login = new Login();
        login.setLogin(employee.getCpf());
        login.setPassword(this.passwordEncoder.encode(this.environment.getProperty("app.default-employee-password")));
        login.setRole(role);
        login.setEmployee(employee);
        repository.save(login);
    }

    public void createDefaultLoginTo(Person person){
        Login login = new Login();

        login.setLogin(person.getCpf());
        login.setPassword(this.passwordEncoder.encode(this.environment.getProperty("app.default-user-password")));
        login.setRole(Roles.USER);
        login.setPerson(person);

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
