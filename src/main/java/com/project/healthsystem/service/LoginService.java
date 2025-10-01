package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.LoginDTO;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.EmployeeRepository;
import com.project.healthsystem.repository.LoginRepository;
import com.project.healthsystem.repository.PersonRepository;
import com.project.healthsystem.validator.EmployeeValidator;
import com.project.healthsystem.validator.LoginValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository repository;
    private final PersonRepository personRepository;
    private final EmployeeRepository employeeRepository;
    private final LoginValidator loginValidator;

    public Login save(LoginDTO loginDTO){
        Optional<Person> person = personRepository.findById(loginDTO.getPersonId());
        Optional<Employee> employee = employeeRepository.findById(loginDTO.getEmployeeId());

        if(person.isEmpty() && employee.isEmpty()){
            return this.repository.save(loginDTO.mappingToLogin());
        } else if(person.isPresent()){
            return repository.save(loginDTO.mappingToLogin(person.get()));
        }

        return repository.save(loginDTO.mappingToLogin(employee.get()));
    }

    public void update(LoginDTO loginDTO, long id){
        loginValidator.validate(id);
        Optional<Login> loginOptional = repository.findById(id);

        Login login = loginOptional.get();
        login.coppingFromLoginDTO(loginDTO);

        loginValidator.validate(login);
        repository.save(login);
    }

    public List<LoginDTO> getAll(){
        List<Login> logins = repository.findAll();
        return logins.stream()
            .map(LoginDTO::new)
            .collect(Collectors.toList());
    }

    public Optional<Login> findById(long id){
        loginValidator.validate(id);
        return this.repository.findById(id);
    }

    public void delete(long id){
        loginValidator.validate(id);
        Optional<Login> loginOptional = repository.findById(id);
        repository.delete(loginOptional.get());
    }
}
