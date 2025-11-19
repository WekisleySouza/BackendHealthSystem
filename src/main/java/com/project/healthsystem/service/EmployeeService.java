package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.EmployeeRequestDTO;
import com.project.healthsystem.controller.mappers.EmployeeMapper;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Roles;
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
    private final LoginService loginService;
    private final EmployeeValidator employeeValidator;
    private final EmployeeMapper employeeMapper;

    public Employee save(EmployeeRequestDTO employeeRequestDTO){
        Employee employee = employeeValidator.validateSave(employeeRequestDTO);
        employee = repository.save(employee);
        loginService.createDefaultLoginTo(employee, Roles.fromLabel(employeeRequestDTO.getRole()));
        return employee;
    }

    public void update(EmployeeRequestDTO employeeRequestDTO, long id){
        var employee = employeeValidator.validateUpdate(employeeRequestDTO, id);
        repository.save(employee);
    }

    public Employee findById(long id){
        return employeeValidator.validateFindById(id);
    }

    public List<EmployeeRequestDTO> getAll(){
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
