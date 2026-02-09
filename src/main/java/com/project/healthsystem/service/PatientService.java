package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.mappers.*;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Roles;
import com.project.healthsystem.repository.PatientRepository;
import com.project.healthsystem.repository.projections.PatientInfoAgentProjection;
import com.project.healthsystem.repository.projections.PatientInfoResponsibleProjection;
import com.project.healthsystem.repository.projections.PatientSimplifiedInfoProjection;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repository;

    private final PatientValidator patientValidator;

    private final PatientMapper patientMapper;
    private final PersonMapper personMapper;
    private final PatientInfoAppointmentProjectionMapper patientInfoAppointmentProjectionMapper;

    private final LoginService loginService;
    private final PersonService personService;
    private final RoleService roleService;
    private final AppointmentService appointmentService;

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

    public List<PatientSimplifiedInfoDTO> getSimplifiedPatients(){
        return repository
            .findAllBy()
            .stream()
            .map(patientProjection -> new PatientSimplifiedInfoDTO(
                patientProjection.getId(),
                patientProjection.getPerson().getName(),
                patientProjection.getPerson().getCpf(),
                patientProjection.getMotherName()
            ))
            .toList();
    }

    public Page<PatientResponseDTO> getAll(
            Integer pageNumber,
            Integer pageLength,
            String name,
            String gender,
            String cpf,
            String phone,
            LocalDate birthday,
            String email,
            String cns,
            String motherName
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<Patient> specs =  null;
        specs = SpecsCommon.addSpec(specs, PatientSpecs.nameLike(name));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.genderEqual(gender));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.cpfLike(cpf));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.phoneLike(phone));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.birthdayEqual(birthday));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.emailLike(email));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.cnsLike(cns));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.motherNameLike(motherName));
        return repository
            .findAll(specs, pageRequest)
            .map(patientMapper::toDto);
    }

    public PatientInfoResponseDTO getPatientInfo(long id){
        patientValidator.validateFindById(id);
        PatientInfoAgentProjection patientInfoAgentProjection = patientValidator.validateFindAgentById(id);
        PatientInfoAgentResponseDTO patientInfoAgentResponseDTO = new PatientInfoAgentResponseDTO(
            patientInfoAgentProjection.getAgent().getId(),
            patientInfoAgentProjection.getAgent().getPerson().getName()
        );
        PatientInfoResponsibleProjection patientInfoResponsibleProjection = patientValidator.validateFindResponsibleById(id);
        PatientInfoResponsibleResponseDTO patientInfoResponsibleResponseDTO = new PatientInfoResponsibleResponseDTO(
            patientInfoResponsibleProjection.getPerson().getId(),
            patientInfoResponsibleProjection.getPerson().getName()
        );
        List<PatientInfoAppointmentResponseDTO> patientInfoAppointmentResponseDTO = patientValidator
            .validateFindAppointmentsById(id)
            .getAppointments()
            .stream()
            .map(patientInfoAppointmentProjectionMapper::toDto)
            .toList();
        List<ConditionResponseDTO> patientConditionResponseDTOS = patientValidator
            .validateFindConditionsById(id)
            .getConditions()
            .stream()
            .map(conditionProjection -> new ConditionResponseDTO(
                conditionProjection.getId(),
                conditionProjection.getSpecification()
            ))
            .toList();
        PatientResponseDTO patient = this.findById(id);

        return new PatientInfoResponseDTO(
            patient.getId(),
            patientInfoAgentResponseDTO,
            patientInfoResponsibleResponseDTO,
            patientConditionResponseDTOS,
            patientInfoAppointmentResponseDTO,
            patient.getName(),
            patient.getGender(),
            patient.getMotherName(),
            patient.getBirthday(),
            patient.getCns(),
            patient.getCpf(),
            patient.getAddress(),
            patient.getPhone(),
            patient.getEmail()
        );
    }

    public Patient getByCpf(String cpf){
        return patientValidator.validateFindByCpf(cpf);
    }

    public Patient getById(long id){
        return patientValidator.validateFindById(id);
    }

    public void delete(long id){
        repository.delete(patientValidator.validateDelete(id));
    }
}
