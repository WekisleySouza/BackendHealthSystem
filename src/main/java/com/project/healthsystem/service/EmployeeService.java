package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.EmployeeDTO;
import com.project.healthsystem.controller.mappers.EmployeeMapper;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.repository.EmployeeRepository;
import com.project.healthsystem.validator.EmployeeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeValidator employeeValidator;
    private final EmployeeMapper employeeMapper;

    public Employee save(EmployeeDTO employeeDTO){
        Employee employee = employeeValidator.validateSave(employeeDTO);
        return repository.save(employee);
    }

    public void update(EmployeeDTO employeeDTO, long id){
        var employee = employeeValidator.validateUpdate(employeeDTO, id);
        repository.save(employee);
    }

    public Employee findById(long id){
        return employeeValidator.validateFindById(id);
    }

    public List<EmployeeDTO> getAll(){
        List<Employee> employees = repository.findAll();
        return employees.stream()
            .map(employeeMapper::toDto)
            .collect(Collectors.toList());
    }

    public void delete(long id){
        Employee employee = employeeValidator.validateDelete(id);
        repository.delete(employee);
    }
}
