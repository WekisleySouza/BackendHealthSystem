package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.dto.basic_requests.PatientCPFRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.PatientRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.PersonBackupInfoDTO;
import com.project.healthsystem.controller.dto.basic_responses.ConditionResponseDTO;
import com.project.healthsystem.controller.dto.basic_responses.PatientResponseDTO;
import com.project.healthsystem.controller.dto.pec_sync.PersonsInfoList;
import com.project.healthsystem.controller.dto.simplified_info.PatientSimplifiedInfoDTO;
import com.project.healthsystem.controller.dto.simplified_info.PatientSimplifiedResponseDTO;
import com.project.healthsystem.controller.mappers.*;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.BackupControlRepository;
import com.project.healthsystem.repository.PatientRepository;
import com.project.healthsystem.repository.projections.PatientInfoAgentProjection;
import com.project.healthsystem.repository.projections.PatientInfoResponsibleProjection;
import com.project.healthsystem.repository.specs.PatientSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.utils.PersonBackupInfo;
import com.project.healthsystem.validator.PatientValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PatientService {

    @Autowired
    @Qualifier("externalJdbcTemplate")
    private JdbcTemplate externalJdbcTemplate;

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

    private final BackupControlRepository backupControlRepository;

    @Transactional
    public Patient save(PatientRequestDTO patientRequestDTO, String token){
        Patient patient = patientValidator.validateSave(patientRequestDTO);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        patient.createdNow();
        patient.setCreatedBy(currentEditor);
        patient.setLastModifiedBy(currentEditor);

        // Save Person
        if(!patientRequestDTO.getCpfNormalized().isBlank() && personService.existsPersonByCpf(patientRequestDTO.getCpfNormalized())){
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

        patient.setOrigin("Vitalya");
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
        Person person = personService.findById(patient.getPerson().getId());
        person = personMapper.updatePersonEntity(person, patientRequestDTO);
        person.updatedNow();
        person.setLastModifiedBy(currentEditor);
        person = personService.save(person);

        patient.setPerson(person);
        repository.save(patient);
    }

    @Transactional
    public void updateCPF(PatientCPFRequestDTO patientCPFRequestDTO, String token){
//        patientValidator.validateUpdateCPF(patientCPFRequestDTO.getCpfNormalized());
        Patient patient = patientValidator.validateFindById(patientCPFRequestDTO.getPatientId());

        // Auditory
        Person person = jwtTokenProvider.getPerson(token);
        patient.setLastModifiedBy(person);
        patient.updatedNow();

        patient.getPerson().setCpf(patientCPFRequestDTO.getCpfNormalized());

        if(loginService.hasLogin(patient.getPerson())){
            loginService.updateLogin(patient.getPerson().getId(), patientCPFRequestDTO.getCpfNormalized());
        } else {
            loginService.createDefaultLoginTo(patient);
            patient.getPerson().addRole(roleService.findByRole(Roles.PATIENT));
        }

        repository.save(patient);
        personService.save(person);
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

    public Page<PatientSimplifiedResponseDTO> getAllSimplified(
            Integer pageNumber,
            Integer pageLength
    ){
        Sort sort = Sort.by("person.name").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        return repository
            .getAllBy(pageRequest)
            .map(projection -> new PatientSimplifiedResponseDTO(
                projection.getId(),
                projection.getPerson().getName()
            ));
    }

    public Page<PatientResponseDTO> getAll(
            Integer pageNumber,
            Integer pageLength,
            String name,
            String gender,
            String cpf,
            LocalDate birthday,
            String email,
            String cns,
            String motherName,
            String teamName,
            String teamINE,
            String microArea,
            String origin,
            String sex,
            String cellPhone,
            String residentialPhone,
            String contactPhone
    ){
        Sort sort = Sort.by("person.name").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        Specification<Patient> specs =  null;
        specs = SpecsCommon.addSpec(specs, PatientSpecs.nameLike(name));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.genderEqual(gender));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.cpfLike(cpf));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.birthdayEqual(birthday));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.emailLike(email));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.cnsLike(cns));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.motherNameLike(motherName));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.teamNameLike(teamName));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.teamINELike(teamINE));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.microAreaLike(microArea));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.originLike(origin));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.sexEqual(sex));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.cellPhoneLike(cellPhone));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.residentialPhoneLike(residentialPhone));
        specs = SpecsCommon.addSpec(specs, PatientSpecs.contactPhoneLike(contactPhone));
        return repository
            .findAll(specs, pageRequest)
            .map(patientMapper::toDto);
    }

    public PatientInfoResponseDTO getPatientInfo(long id){
        patientValidator.validateFindById(id);
        PatientInfoAgentProjection patientInfoAgentProjection = patientValidator.validateFindAgentById(id);
        PatientInfoAgentResponseDTO patientInfoAgentResponseDTO = new PatientInfoAgentResponseDTO(
            patientInfoAgentProjection.getAgentId(),
            patientInfoAgentProjection.getAgentName()
        );
        PatientInfoResponsibleProjection patientInfoResponsibleProjection = patientValidator.validateFindResponsibleById(id);
        PatientInfoResponsibleResponseDTO patientInfoResponsibleResponseDTO = new PatientInfoResponsibleResponseDTO(
            patientInfoResponsibleProjection.getResponsibleId(),
            patientInfoResponsibleProjection.getResponsibleName()
        );
        List<PatientInfoAppointmentResponseDTO> patientInfoAppointmentResponseDTO = appointmentService
            .findAppointmentsByPatientId(id)
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
            patient.getTeamName(),
            patient.getTeamINE(),
            patient.getMicroArea(),
            patient.getOrigin(),
            patientInfoAgentResponseDTO,
            patientInfoResponsibleResponseDTO,
            patientConditionResponseDTOS,
            patientInfoAppointmentResponseDTO,
            patient.getName(),
            patient.getGender(),
            patient.getSex(),
            patient.getMotherName(),
            patient.getBirthday(),
            patient.getCns(),
            patient.getCpf(),
            patient.getAddress(),
            patient.getCellPhone(),
            patient.getResidentialPhone(),
            patient.getContactPhone(),
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

    public void syncExternalDataBase(List<PersonBackupInfoDTO> personsInfoList){
        int receivedPatients = 0;
        int newPatients = 0;
        int patientsUpdatedById = 0;
        int patientsUpdatedByCpf = 0;
        int patientsUpdatedByCns = 0;
        int patientsUpdatedByName = 0;

        BackupControl backupControl = new BackupControl();
        backupControl.startBackup();

        for(PersonBackupInfoDTO personData :  personsInfoList){
            receivedPatients++;
            if (personData.hasCitizenSeqId()) {
                Patient patient;

                if (repository.existsByPersonPersonSequenceId(personData.getCitizenSeqId())) { // Atualizar pelo id de sequência único
                    List<Patient> patients = repository.findByPersonPersonSequenceId(personData.getCitizenSeqId());
                    patient = personData.getPersonToUpdate(patients);
                    patientsUpdatedById++;

                } else if (personData.hasCpf() && repository.existsByPersonCpf(personData.getCpf())) { // Atualizar pelo cpf
                    List<Patient> patients = repository.findByPersonCpf(personData.getCpf());
                    patient = personData.getPersonToUpdate(patients);
                    patientsUpdatedByCpf++;

                } else if (personData.hasCns() && repository.existsByCns(personData.getCns())) { // Atualizar pelo cns
                    List<Patient> patients = repository.findByCns(personData.getCns());
                    patient = personData.getPersonToUpdate(patients);
                    patientsUpdatedByCns++;

                } else if (repository.existsByPersonNameIgnoreCase(personData.getPatientName()) && backupControlRepository.existsBy()) { // Atualizar pelo nome se for a primeira atualizaçao
                    List<Patient> patients = repository.findByPersonNameIgnoreCase(personData.getPatientName());
                    patient = personData.getPersonToUpdate(patients);
                    patientsUpdatedByName++;

                } else { // Criar novo. Isso, no caso de não existir um registro.
                    patient = personData.getPersonToSave();
                    newPatients++;
                }

                patient.getPerson().addRole(roleService.findByRole(Roles.PATIENT));
                personService.save(patient.getPerson());
                repository.save(patient);
                loginService.createDefaultLoginTo(patient);
            }
        }

        System.out.println("Pacientes recebidos: " + receivedPatients);
        System.out.println("Novos pacientes: " + newPatients);
        System.out.println("Atualizado por id: " + patientsUpdatedById);
        System.out.println("Atualizado por CPF: " + patientsUpdatedByCpf);
        System.out.println("Atualizado por CNS: " + patientsUpdatedByCns);
        System.out.println("Atualizado por nome: " + patientsUpdatedByName);
        System.out.println("Atualizado por nome: " + patientsUpdatedByName);

        backupControl.setTotalCreated(Integer.toUnsignedLong(newPatients));
        backupControl.setTotalUpdated(
                Integer.toUnsignedLong(
                    patientsUpdatedByCns +
                    patientsUpdatedById +
                    patientsUpdatedByCpf +
                    patientsUpdatedByName
                )
        );
        backupControl.finishBackup();
        backupControlRepository.save(backupControl);
    }
}