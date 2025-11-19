package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.LoginRequestDTO;
import com.project.healthsystem.controller.mappers.LoginMapper;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository repository;
    private final LoginValidator loginValidator;
    private final LoginMapper loginMapper;
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
        login.setPassword(this.passwordEncoder.encode(this.environment.getProperty("app.default-admin.password")));
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

    public void update(LoginRequestDTO loginRequestDTO, long id){
        loginValidator.validate(id);
        Optional<Login> loginOptional = repository.findById(id);

        Login login = loginMapper.toEntityWhenHasId(loginOptional.get(), loginRequestDTO);
        login.setPassword(passwordEncoder.encode(loginRequestDTO.getPassword()));

        loginValidator.validate(login);
        repository.save(login);
    }

    public List<LoginRequestDTO> getAll(){
        List<Login> logins = repository.findAll();
        return logins.stream()
            .map(loginMapper::toDto)
            .collect(Collectors.toList());
    }

    public Optional<Login> findById(long id){
        loginValidator.validate(id);
        return this.repository.findById(id);
    }

    public Login findByLogin(String login){
        return this.repository.findByLogin(login)
            .orElse(null);
    }

    public void delete(long id){
        loginValidator.validate(id);
        Login login = repository.getReferenceById(id);
        repository.delete(login);
    }
}
