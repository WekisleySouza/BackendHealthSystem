package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.LoginDTO;
import com.project.healthsystem.controller.mappers.LoginMapper;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.EmployeeRepository;
import com.project.healthsystem.repository.LoginRepository;
import com.project.healthsystem.repository.PersonRepository;
import com.project.healthsystem.validator.LoginValidator;
import lombok.RequiredArgsConstructor;
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
    private final LoginMapper loginMapper;

    public LoginDTO save(LoginDTO loginDTO){
        Optional<Person> person = personRepository.findById(loginDTO.getUserId());
        Optional<Employee> employee = employeeRepository.findById(loginDTO.getUserId());

        Login login = loginMapper.toEntity(loginDTO);

        if(person.isPresent()){
            login.setPerson(person.get());
        } else if(employee.isPresent()){
            login.setEmployee(employee.get());
        }

        return loginMapper.toDto(repository.save(login));
    }

    public void update(LoginDTO loginDTO, long id){
        loginValidator.validate(id);
        Optional<Login> loginOptional = repository.findById(id);

        Login login = loginMapper.toEntityWhenHasId(loginOptional.get(), loginDTO);

        loginValidator.validate(login);
        repository.save(login);
    }

    public List<LoginDTO> getAll(){
        List<Login> logins = repository.findAll();
        return logins.stream()
            .map(loginMapper::toDto)
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
