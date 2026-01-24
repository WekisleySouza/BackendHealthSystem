package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.EmployeeRequestDTO;
import com.project.healthsystem.controller.dto.EmployeeResponseDTO;
import com.project.healthsystem.controller.mappers.EmployeeMapper;
import com.project.healthsystem.controller.mappers.PersonMapper;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Roles;
import com.project.healthsystem.repository.EmployeeRepository;
import com.project.healthsystem.repository.specs.EmployeeSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.EmployeeValidator;
import jakarta.transaction.Transactional;
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
    private final EmployeeValidator employeeValidator;
    private final EmployeeMapper employeeMapper;
    private final PersonMapper personMapper;

    private final LoginService loginService;
    private final PersonService personService;
    private final RoleService roleService;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Employee save(EmployeeRequestDTO employeeRequestDTO, String token){
        Employee employee = employeeValidator.validateSave(employeeRequestDTO);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        employee.createdNow();
        employee.setCreatedBy(currentEditor);
        employee.setLastModifiedBy(currentEditor);

        // Save Person
        if(personService.existsPersonByCpf(employeeRequestDTO.getCpfNormalized())){
            Person person = personService.getReferenceByCpf(employeeRequestDTO.getCpfNormalized());
            person.addRole(roleService.findByRole(Roles.fromLabel(employeeRequestDTO.getRole())));
            employee.setPerson(person);
        } else {
            Person person = personMapper.toPersonEntity(employeeRequestDTO);
            person
                .addRole(roleService.findByRole(Roles.PATIENT))
                .addRole(roleService.findByRole(Roles.fromLabel(employeeRequestDTO.getRole())));
            person.setCreatedBy(currentEditor);
            person.setLastModifiedBy(currentEditor);
            person.createdNow();
            Person savedPerson = personService.save(person);

            employee.setPerson(savedPerson);
        }

        employee = repository.save(employee);
        loginService.createDefaultLoginTo(employee);
        return employee;
    }

    @Transactional
    public void update(EmployeeRequestDTO employeeRequestDTO, long id, String token){
        var employee = employeeValidator.validateUpdate(employeeRequestDTO, id);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        employee.setLastModifiedBy(currentEditor);
        employee.updatedNow();

        // Saving Person
        Person person = personService.findByCpf(employeeRequestDTO.getCpfNormalized());
        person = personMapper.updatePersonEntity(person, employeeRequestDTO);
        person.updatedNow();
        person.setLastModifiedBy(currentEditor);
        person = personService.save(person);

        employee.setPerson(person);
        repository.save(employee);
    }

    public Employee findById(long id){
        return employeeValidator.validateFindById(id);
    }

    public Employee findByCpf(String cpf) {
        return employeeValidator.validateFindByCpf(cpf);
    }

    public Page<EmployeeResponseDTO> getAll(
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

    @Transactional
    public void delete(long id){
        Employee employee = employeeValidator.validateDelete(id);

        // Removing role
        Person person = employee.getPerson();
        person.removeRole(Roles.EMPLOYEE)
            .removeRole(Roles.MANAGER)
            .removeRole(Roles.ADMIN);
        personService.save(person);

        repository.delete(employee);
    }
}
