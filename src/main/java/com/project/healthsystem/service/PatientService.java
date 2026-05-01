package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.dto.basic_requests.PatientRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.ConditionResponseDTO;
import com.project.healthsystem.controller.dto.basic_responses.PatientResponseDTO;
import com.project.healthsystem.controller.dto.simplified_info.PatientSimplifiedInfoDTO;
import com.project.healthsystem.controller.dto.simplified_info.PatientSimplifiedResponseDTO;
import com.project.healthsystem.controller.mappers.*;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.PatientRepository;
import com.project.healthsystem.repository.projections.PatientInfoAgentProjection;
import com.project.healthsystem.repository.projections.PatientInfoResponsibleProjection;
import com.project.healthsystem.repository.specs.PatientSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.PatientValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Transactional
    public void importCsv(MultipartFile file) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;

            // Pular header
            for (int i = 0; i < 1; i++) {
                reader.readLine();
            }

            while ((line = reader.readLine()) != null) {

                String[] columns = line.split(";");

                String cpfCNS = columns[4].replaceAll("\\D", "");
                String lastUpdate = columns[13];

                Person person = new Person();
                person.setAddress(columns[3]);
                person.setName(columns[5]);
                person.setSex(Sex.fromLabel(columns[7]));

                if(columns[8].equals("-")) {
                    person.setGender(Gender.UNDEFINED);
                }
                else {
                   person.setGender(Gender.fromLabel(columns[8]));
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                person.setBirthday(LocalDate.parse(columns[9], formatter));
                person.setLastUpdate(LocalDate.parse(lastUpdate, formatter).atStartOfDay());
                person.setCellPhone(columns[10]);
                person.setResidentialPhone(columns[11]);
                person.setContactPhone(columns[12]);

                Patient patient = new Patient();
                patient.setTeamName(columns[0]);
                patient.setTeamINE(columns[1].replaceAll("\\D", ""));
                patient.setMicroArea(columns[2].replaceAll("\\D", ""));
                patient.setOrigin(columns[14]);

                if(!(cpfCNS.isBlank())){
                    // Se número de dígitos maior que 11, é CNS
                    if(cpfCNS.length() > 11){
                        patient.setCns(cpfCNS);
                    } else {
                        person.setCpf(cpfCNS);
                    }
                }

                personService.save(person);

                patient.setPerson(person);

                repository.save(patient);

                loginService.createDefaultLoginTo(patient);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar CSV" + e.getMessage());
        }
    }

    @Transactional
    public void importESUSCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;

            // Pular header
            reader.readLine();

            while ((line = reader.readLine()) != null) {

                String[] columns = line.split(";", -1);
                Patient patient = new Patient();
                Person person = new Person();

                String citizenSequenceId = columns[0];
                String uniqueCitizenId = columns[1];
                String citizenName = columns[2];
                String address = columns[3];
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate birthDate = LocalDate.parse(columns[4], formatter);
                String mobilePhone = columns[5];
                String contactPhone = columns[6];
                String homePhone = columns[7];
                String cpf = columns[8].replaceAll("\\D", "");
                String email = columns[9];
                String gender = columns[10];
                String sex = columns[11];
                String cns = columns[12].replaceAll("\\D", "");

                person.setPersonSequenceId(citizenSequenceId);
                person.setUniquePersonId(uniqueCitizenId);
                person.setName(citizenName);
                person.setCpf(cpf);
                person.setCellPhone(mobilePhone);
                person.setResidentialPhone(homePhone);
                person.setContactPhone(contactPhone);
                person.setAddress(address);
                person.setBirthday(birthDate);
                person.setEmail(email);

                if(gender.equals("BISSEXUAL")){
                    person.setGender(Gender.BISEXUAL);
                } else if (gender.equals("GAY")) {
                    person.setGender(Gender.HOMOSEXUAL);
                } else if (gender.equals("HETEROSSEXUAL")) {
                    person.setGender(Gender.MALE);
                } else if (gender.equals("HOMOSSEXUAL")) {
                    person.setGender(Gender.HOMOSEXUAL);
                } else if (gender.equals("LESBICA")) {
                    person.setGender(Gender.HOMOSEXUAL);
                } else {
                    person.setGender(Gender.NOT_INFORMED);
                }
                if(sex.equals("FEMININO")){
                    person.setSex(Sex.FEMALE);
                } else if (sex.equals("INDETERMINADO")) {
                    person.setSex(Sex.UNKNOW);
                } else {
                    person.setSex(Sex.MALE);
                }

                patient.setCns(cns);
                patient.setPerson(person);

                List<Patient> patientByName = repository.findByPersonName(citizenName);
                List<Patient> patientByCpf = repository.findByPersonCpf(cpf);
                List<Patient> patientByCns = repository.findByCns(cns);

                if(!patientByName.isEmpty()){
                    this.updateIfEmpty(patientByName.get(0), patient);
                    personService.save(patientByName.get(0).getPerson());
                    repository.save(patientByName.get(0));
                } else if (!patientByCpf.isEmpty()) {
                    this.updateIfEmpty(patientByCpf.get(0), patient);
                    personService.save(patientByCpf.get(0).getPerson());
                    repository.save(patientByCpf.get(0));
                } else if (!patientByCns.isEmpty()) {
                    this.updateIfEmpty(patientByCns.get(0), patient);
                    personService.save(patientByCns.get(0).getPerson());
                    repository.save(patientByCns.get(0));
                } else {
                    personService.save(person);
                    repository.save(patient);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar CSV" + e.getMessage());
        }
    }

    private void updateIfEmpty(Patient target, Patient source) {
        if (isEmpty(target.getPerson().getName()) && !isEmpty(source.getPerson().getName())) {
            target.getPerson().setName(source.getPerson().getName());
        }
        if (isEmpty(target.getCns()) && !isEmpty(source.getCns())) {
            target.setCns(source.getCns());
        }
        if (isEmpty(target.getPerson().getCpf()) && !isEmpty(source.getPerson().getCpf())) {
            target.getPerson().setCpf(source.getPerson().getCpf());
        }
        if (isEmpty(target.getPerson().getCellPhone()) && !isEmpty(source.getPerson().getCellPhone())) {
            target.getPerson().setCellPhone(source.getPerson().getCellPhone());
        }
        if (isEmpty(target.getPerson().getResidentialPhone()) && !isEmpty(source.getPerson().getResidentialPhone())) {
            target.getPerson().setResidentialPhone(source.getPerson().getResidentialPhone());
        }
        if (isEmpty(target.getPerson().getContactPhone()) && !isEmpty(source.getPerson().getContactPhone())) {
            target.getPerson().setContactPhone(source.getPerson().getContactPhone());
        }
        if (isEmpty(target.getPerson().getAddress()) && !isEmpty(source.getPerson().getAddress())) {
            target.getPerson().setAddress(source.getPerson().getAddress());
        }
        if (target.getPerson().getBirthday() == null && source.getPerson().getBirthday() != null) {
            target.getPerson().setBirthday(source.getPerson().getBirthday());
        }
        if (isEmpty(target.getPerson().getEmail()) && !isEmpty(source.getPerson().getEmail())) {
            target.getPerson().setEmail(source.getPerson().getEmail());
        }
        if (target.getPerson().getGender() == Gender.NOT_INFORMED && source.getPerson().getGender() != null) {
            target.getPerson().setGender(source.getPerson().getGender());
        }
        if (target.getPerson().getSex() == Sex.NOT_INFORMED && source.getPerson().getSex() != null) {
            target.getPerson().setSex(source.getPerson().getSex());
        }
        if (isEmpty(target.getPerson().getUniquePersonId()) && !isEmpty(source.getPerson().getUniquePersonId())) {
            target.getPerson().setUniquePersonId(source.getPerson().getUniquePersonId());
        }
        if (isEmpty(target.getPerson().getPersonSequenceId()) && !isEmpty(source.getPerson().getPersonSequenceId())) {
            target.getPerson().setPersonSequenceId(source.getPerson().getPersonSequenceId());
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}

