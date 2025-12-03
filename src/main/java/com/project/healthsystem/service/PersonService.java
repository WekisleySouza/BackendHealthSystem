package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.PersonRequestDTO;
import com.project.healthsystem.controller.dto.PersonResponseDTO;
import com.project.healthsystem.controller.mappers.PersonMapper;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.PersonRepository;
import com.project.healthsystem.repository.specs.PersonSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.PersonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository repository;
    private final LoginService loginService;
    private final PersonValidator personValidator;
    private final PersonMapper personMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public Person save(PersonRequestDTO personRequestDTO, String token){
        Person person = personValidator.validateSave(personRequestDTO);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        person.createdNow();
        person.setCreatedBy(currentEditor);
        person.setLastModifiedBy(currentEditor);
        person = repository.save(person);
        loginService.createDefaultLoginTo(person);
        return person;
    }

    public void update(PersonRequestDTO personRequestDTO, long id, String token){
        Person person = personValidator.validateUpdate(personRequestDTO, id);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        person.createdNow();
        person.setCreatedBy(currentEditor);
        person.setLastModifiedBy(currentEditor);
        repository.save(person);
    }

    public PersonResponseDTO findById(long id){
        return personMapper.toDto(personValidator.validateFindById(id));
    }

    public Page<PersonResponseDTO> getAll(
            Integer pageNumber,
            Integer pageLength,
            String name,
            String cpf,
            String phone,
            LocalDate birthday,
            String email,
            String cns,
            String motherName
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<Person> specs =  null;
        specs = SpecsCommon.addSpec(specs, PersonSpecs.nameEqual(name));
        specs = SpecsCommon.addSpec(specs, PersonSpecs.cpfEqual(cpf));
        specs = SpecsCommon.addSpec(specs, PersonSpecs.phoneEqual(phone));
        specs = SpecsCommon.addSpec(specs, PersonSpecs.birthdayEqual(birthday));
        specs = SpecsCommon.addSpec(specs, PersonSpecs.emailEqual(email));
        specs = SpecsCommon.addSpec(specs, PersonSpecs.cnsEqual(cns));
        specs = SpecsCommon.addSpec(specs, PersonSpecs.motherNameEqual(motherName));
        return repository
            .findAll(specs, pageRequest)
            .map(personMapper::toDto);
    }

    public void delete(long id){
        repository.delete(personValidator.validateDelete(id));
    }
}
