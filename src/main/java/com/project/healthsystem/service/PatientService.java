package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.PatientRequestDTO;
import com.project.healthsystem.controller.dto.PatientResponseDTO;
import com.project.healthsystem.controller.mappers.PatientMapper;
import com.project.healthsystem.controller.mappers.PersonMapper;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Roles;
import com.project.healthsystem.repository.PatientRepository;
import com.project.healthsystem.repository.specs.PatientSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.PatientValidator;
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
public class PatientService {

    private final PatientRepository repository;
    private final LoginService loginService;
    private final PatientValidator patientValidator;
    private final PatientMapper patientMapper;
    private final PersonMapper personMapper;

    private final PersonService personService;
    private final RoleService roleService;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Patient save(PatientRequestDTO patientRequestDTO, String token){
        Patient patient = patientValidator.validateSave(patientRequestDTO);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        patient.createdNow();
        patient.setCreatedBy(currentEditor);
        patient.setLastModifiedBy(currentEditor);

        // Save Person
        if(personService.existsPersonByCpf(patientRequestDTO.getCpfNormalized())){
            Person person = personService.getReferenceByCpf(patientRequestDTO.getCpfNormalized());
            person.addRole(roleService.findByRole(Roles.PATIENT));
            patient.setPerson(person);
        } else {
            Person person = personMapper.toPersonEntity(patientRequestDTO);
            person
                .addRole(roleService.findByRole(Roles.PATIENT));
            person.setCreatedBy(currentEditor);
            person.setLastModifiedBy(currentEditor);
            person.createdNow();
            Person savedPerson = personService.save(person);

            patient.setPerson(savedPerson);
        }

        patient = repository.save(patient);
        loginService.createDefaultLoginTo(patient);
        return patient;
    }


    @Transactional
    public void update(PatientRequestDTO patientRequestDTO, long id, String token){
        Patient patient = patientValidator.validateUpdate(patientRequestDTO, id);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        patient.createdNow();
        patient.setCreatedBy(currentEditor);
        patient.setLastModifiedBy(currentEditor);

        // Saving Person
        Person person = personService.findByCpf(patientRequestDTO.getCpfNormalized());
        person = personMapper.updatePersonEntity(person, patientRequestDTO);
        person.updatedNow();
        person.setLastModifiedBy(currentEditor);
        person = personService.save(person);

        patient.setPerson(person);
        repository.save(patient);
    }

    public PatientResponseDTO findById(long id){
        Patient patient = patientValidator.validateFindById(id);
        return patientMapper.toDto(patient);
    }

    public Page<PatientResponseDTO> getAll(
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
        Specification<Patient> specs =  null;
        specs = SpecsCommon.addSpec(specs, PatientSpecs.nameEqual(name));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.cpfEqual(cpf));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.phoneEqual(phone));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.birthdayEqual(birthday));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.emailEqual(email));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.cnsEqual(cns));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.motherNameEqual(motherName));
        return repository
            .findAll(specs, pageRequest)
            .map(patientMapper::toDto);
    }

    public Patient getByCpf(String cpf){
        return patientValidator.validateFindByCpf(cpf);
    }

    public void delete(long id){
        repository.delete(patientValidator.validateDelete(id));
    }
}
