package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.EmployeeDTO;
import com.project.healthsystem.controller.mappers.EmployeeMapper;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeValidator {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public Employee validateSave(EmployeeDTO employeeDTO){
        return employeeMapper.toEntity(employeeDTO);
    }

    public Employee validateUpdate(EmployeeDTO employeeDTO, long id){
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Funcionário não encontrado!"));
        employee = employeeMapper.toEntityWhenHasId(employee, employeeDTO);
        return employee;
    }

    public Employee validateFindById(long id){
        return employeeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Funcionário não encontrado!"));
    }

    public Employee validateDelete(long id){
        return employeeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Funcionário não encontrado!"));
    }
}
