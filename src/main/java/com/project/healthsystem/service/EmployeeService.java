package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.EmployeeDTO;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.repository.EmployeeRepository;
import com.project.healthsystem.validator.EmployeeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeValidator employeeValidator;

    public Employee save(Employee employee){
        employeeValidator.validate(employee);
        return repository.save(employee);
    }

    public void update(EmployeeDTO employeeDTO, long id){
        employeeValidator.validate(id);

        Optional<Employee> employeeOptional = repository.findById(id);
        var employee = employeeOptional.get();
        employee.mappingFromEmployeeDTO(employeeDTO);

        employeeValidator.validate(employee);
        repository.save(employee);
    }

    public Optional<Employee> findById(long id){
        employeeValidator.validate(id);
        return repository.findById(id);
    }

    public List<EmployeeDTO> getAll(){
        List<Employee> employees = repository.findAll();
        return employees.stream()
            .map(EmployeeDTO::new)
            .collect(Collectors.toList());
    }

    public void delete(long id){
        employeeValidator.validate(id);
        Optional<Employee> employeeOptional = repository.findById(id);
        repository.delete(employeeOptional.get());
    }
}
