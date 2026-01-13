package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ProfessionalRequestDTO;
import com.project.healthsystem.controller.dto.ProfessionalResponseDTO;
import com.project.healthsystem.controller.mappers.ProfessionalMapper;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.ProfessionalRepository;
import com.project.healthsystem.repository.specs.ProfessionalSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.ProfessionalValidator;
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
public class ProfessionalService {

    private final ProfessionalRepository repository;
    private final ProfessionalValidator professionalValidator;
    private final ProfessionalMapper professionalMapper;
    private final PersonService personService;
    private final RoleService roleService;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Professional save(ProfessionalRequestDTO professionalRequestDTO, String token){
        Professional professional = professionalValidator.validateSave(professionalRequestDTO);

        // Auditory data setting
        Person currentEditor = jwtTokenProvider.getPerson(token);
        professional.createdNow();
        professional.setCreatedBy(currentEditor);
        professional.setLastModifiedBy(currentEditor);

        if(personService.existsPersonByCpf(professionalRequestDTO.getCpfNormalized())){

            Person person = personService.getReferenceByCpf(professionalRequestDTO.getCpfNormalized());
            professional.setPerson(person);
        } else {
            Person person = new Person();
            person.setName(professionalRequestDTO.getName());
            person.setCpf(professionalRequestDTO.getCpfNormalized());
            person.setBirthday(professionalRequestDTO.getBirthday());
            person.setEmail(professionalRequestDTO.getEmail());
            person.setPhone(professionalRequestDTO.getPhone());
            person.addRole(roleService.findByRole(Roles.USER));
            person.setCreatedBy(currentEditor);
            person.setLastModifiedBy(currentEditor);
            person.createdNow();
            Person savedPerson = personService.save(person);

            professional.setPerson(savedPerson);
        }

        return repository.save(professional);
    }

    @Transactional
    public void update(ProfessionalRequestDTO professionalRequestDTO, long id, String token){
        Professional professional = professionalValidator.validateUpdate(professionalRequestDTO, id);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        professional.setLastModifiedBy(currentEditor);
        professional.updatedNow();

        // Saving Person
        Person person = personService.findByCpf(professionalRequestDTO.getCpfNormalized());
        person.setName(professionalRequestDTO.getName());
        person.setBirthday(professionalRequestDTO.getBirthday());
        person.setEmail(professionalRequestDTO.getEmail());
        person.setPhone(professionalRequestDTO.getPhone());
        person.updatedNow();
        person.setLastModifiedBy(currentEditor);
        person = personService.save(person);

        professional.setPerson(person);
        repository.save(professional);
    }

    public Page<ProfessionalResponseDTO> getAll(
            Integer pageNumber,
            Integer pageLength,
            String name,
            String cpf,
            String phone,
            LocalDate birthday,
            String email
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<Professional> specs = null;
        specs = SpecsCommon.addSpec(specs, ProfessionalSpecs.nameEqual(name));
        specs = SpecsCommon.addSpec(specs, ProfessionalSpecs.cpfEqual(cpf));
        specs = SpecsCommon.addSpec(specs, ProfessionalSpecs.phoneEqual(phone));
        specs = SpecsCommon.addSpec(specs, ProfessionalSpecs.birthdayEqual(birthday));
        specs = SpecsCommon.addSpec(specs, ProfessionalSpecs.emailEqual(email));
        return repository
            .findAll(specs, pageRequest)
            .map(professionalMapper::toDto);
    }

    public Professional findById(long id){
        return professionalValidator.validateFindById(id);
    }

    public void delete(long id){
        Professional professional = professionalValidator.validateDelete(id);
        repository.delete(professional);
    }
}
