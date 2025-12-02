package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.EmployeeRequestDTO;
import com.project.healthsystem.controller.mappers.EmployeeMapper;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Roles;
import com.project.healthsystem.repository.EmployeeRepository;
import com.project.healthsystem.repository.specs.EmployeeSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.EmployeeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final LoginService loginService;
    private final EmployeeValidator employeeValidator;
    private final EmployeeMapper employeeMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public Employee save(EmployeeRequestDTO employeeRequestDTO, String token){
        Employee employee = employeeValidator.validateSave(employeeRequestDTO);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        employee.createdNow();
        employee.setCreatedBy(currentEditor);
        employee.setLastModifiedBy(currentEditor);
        employee = repository.save(employee);
        loginService.createDefaultLoginTo(employee, Roles.fromLabel(employeeRequestDTO.getRole()));
        return employee;
    }

    public void update(EmployeeRequestDTO employeeRequestDTO, long id, String token){
        var employee = employeeValidator.validateUpdate(employeeRequestDTO, id);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        employee.setLastModifiedBy(currentEditor);
        employee.updatedNow();
        repository.save(employee);
    }

    public Employee findById(long id){
        return employeeValidator.validateFindById(id);
    }

    public Page<EmployeeRequestDTO> getAll(
            Integer pageNumber,
            Integer pageLength,
            String name,
            String cpf,
            String phone,
            LocalDate birthday,
            String email
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<Employee> specs = null;
        specs = SpecsCommon.addSpec(specs, EmployeeSpecs.nameEqual(name));
        specs = SpecsCommon.addSpec(specs, EmployeeSpecs.cpfEqual(cpf));
        specs = SpecsCommon.addSpec(specs, EmployeeSpecs.phoneEqual(phone));
        specs = SpecsCommon.addSpec(specs, EmployeeSpecs.birthdayEqual(birthday));
        specs = SpecsCommon.addSpec(specs, EmployeeSpecs.emailEqual(email));
        return repository
            .findAll(specs, pageRequest)
            .map(employeeMapper::toDto);
    }

    public void delete(long id){
        Employee employee = employeeValidator.validateDelete(id);
        repository.delete(employee);
    }
}
