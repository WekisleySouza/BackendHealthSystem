package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.AgentRequestDTO;
import com.project.healthsystem.controller.dto.AgentResponseDTO;
import com.project.healthsystem.controller.dto.simplified_info.AgentSimplifiedResponseDTO;
import com.project.healthsystem.controller.mappers.AgentMapper;
import com.project.healthsystem.controller.mappers.PersonMapper;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Roles;
import com.project.healthsystem.repository.AgentRepository;
import com.project.healthsystem.repository.specs.AgentSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.AgentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository repository;
    private final AgentValidator agentValidator;
    private final AgentMapper agentMapper;
    private final PersonMapper personMapper;

    private final PersonService personService;
    private final RoleService roleService;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Agent save(AgentRequestDTO agentRequestDTO, String token){
        Agent agent = agentValidator.validateSave(agentRequestDTO);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        agent.createdNow();
        agent.setCreatedBy(currentEditor);
        agent.setLastModifiedBy(currentEditor);

        // Save Person
        if(personService.existsPersonByCpf(agentRequestDTO.getCpfNormalized())){
            Person person = personService.getReferenceByCpf(agentRequestDTO.getCpfNormalized());
            person.addRole(roleService.findByRole(Roles.PATIENT));
            agent.setPerson(person);
        }
        else {
            Person person = personMapper.toPersonEntity(agentRequestDTO);
            person
                .addRole(roleService.findByRole(Roles.PATIENT));
            person.setCreatedBy(currentEditor);
            person.setLastModifiedBy(currentEditor);
            person.createdNow();
            Person savedPerson = personService.save(person);

            agent.setPerson(savedPerson);
        }

        return this.repository.save(agent);
    }

    @Transactional
    public void update(AgentRequestDTO agentRequestDTO, long id, String token){
        Agent agent = agentValidator.validateUpdate(agentRequestDTO, id);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        agent.setLastModifiedBy(currentEditor);
        agent.updatedNow();

        // Saving Person
        Person person = personService.findByCpf(agentRequestDTO.getCpfNormalized());
        person = personMapper.updatePersonEntity(person, agentRequestDTO);
        person.updatedNow();
        person.setLastModifiedBy(currentEditor);
        person = personService.save(person);

        agent.setPerson(person);
        this.repository.save(agent);
    }

    public AgentResponseDTO findById(long id){
        Agent agent = agentValidator.validateFindById(id);
        return agentMapper.toDto(agent);
    }

    public Page<AgentSimplifiedResponseDTO> getAllSimplified(
        Integer pageNumber,
        Integer pageLength
    ){
        Sort sort = Sort.by("person.name").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        return repository
            .findAllBy(pageRequest)
            .map(agentProjection -> new AgentSimplifiedResponseDTO(
                agentProjection.getId(),
                agentProjection.getPerson().getName()
            ));
    }

    public Page<AgentResponseDTO> getAll(
        Integer pageNumber,
        Integer pageLength,
        String name,
        String gender,
        String cpf,
        LocalDate birthday,
        String email,
        String sex,
        String cellPhone,
        String residentialPhone,
        String contactPhone
    ){
        Sort sort = Sort.by("person.name").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        Specification<Agent> specs =  null;
        specs = SpecsCommon.addSpec(specs, AgentSpecs.nameLike(name));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.genderEqual(gender));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.cpfLike(cpf));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.birthdayEqual(birthday));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.emailLike(email));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.sexEqual(sex));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.cellPhoneLike(cellPhone));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.residentialPhoneLike(residentialPhone));
        specs = SpecsCommon.addSpec(specs, AgentSpecs.contactPhoneLike(contactPhone));
        return repository
            .findAll(specs, pageRequest)
            .map(agentMapper::toDto);
    }

    public void delete(long id){
        Agent agent = agentValidator.validateDelete(id);
        repository.delete(agent);
    }
}
