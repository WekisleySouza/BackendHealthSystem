package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.PatientRequestDTO;
import com.project.healthsystem.controller.mappers.PatientMapper;
import com.project.healthsystem.exceptions.DuplicatedRegisterException;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.repository.AgentRepository;
import com.project.healthsystem.repository.PatientRepository;
import com.project.healthsystem.repository.projections.*;
import com.project.healthsystem.service.ConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PatientValidator {

    private final PatientRepository patientRepository;
    private final AgentRepository agentRepository;
    private final ConditionService conditionService;

    private final PatientMapper patientMapper;

    public Patient validateSave(PatientRequestDTO patientRequestDTO){
        if (patientRepository.existsByPersonCpf(patientRequestDTO.getCpfNormalized())){
            throw new DuplicatedRegisterException("Cpf já cadastrado!");
        }

        Agent agent = agentRepository.findById(patientRequestDTO.getAgentId())
            .orElseThrow(() -> new InvalidDataException("Agent inválido!"));

        Patient patient = patientMapper.toEntity(patientRequestDTO);
        patient.setAgent(agent);

        if(patientRequestDTO.getResponsibleId() != null){
            Patient responsible = patientRepository.findById(patientRequestDTO.getResponsibleId())
                .orElse(null);
            patient.setResponsible(responsible);
        }
        if(patientRequestDTO.getConditionsId() != null && !patientRequestDTO.getConditionsId().isEmpty()){
            List<Condition> conditions = conditionService.findByIds(patientRequestDTO.getConditionsId());
            patient.setConditions(conditions);
        }

        return patient;
    }

    public Patient validateUpdate(PatientRequestDTO patientRequestDTO, long id){
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Paciente não encontrada!"));
        Agent agent = agentRepository.findById(patientRequestDTO.getAgentId())
                .orElseThrow(() -> new InvalidDataException("Agent inválido!"));

        patient = patientMapper.toEntityWhenHasId(patient, patientRequestDTO);
        patient.setAgent(agent);

        if(patientRequestDTO.getResponsibleId() != null){
            Patient responsible = patientRepository.findById(patientRequestDTO.getResponsibleId())
                    .orElse(null);
            patient.setResponsible(responsible);
        }
        if(patientRequestDTO.getConditionsId() != null && !patientRequestDTO.getConditionsId().isEmpty()){
            List<Condition> conditions = conditionService.findByIds(patientRequestDTO.getConditionsId());
            patient.setConditions(conditions);
        }

        return patient;
    }

    public Patient validateFindById(long id){
        return patientRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Paciente não encontrada!"));
    }

    public PatientInfoResponsibleProjection validateFindResponsibleById(long id){
        return patientRepository.findResponsibleById(id)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado!"));
    }

    public PatientInfoAgentProjection validateFindAgentById(long id){
        return patientRepository.findAgentById(id)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado!"));
    }

    public PatientInfoConditionsProjection validateFindConditionsById(long id){
        return patientRepository.findConditionsById(id)
            .orElseThrow(() -> new NotFoundException("Paciente não encontrado!"));
    }

    public Patient validateFindByCpf(String cpf){
        return patientRepository.findByPersonCpf(cpf)
            .orElseThrow(() -> new NotFoundException("Paciente não encontrado!"));
    }

    public Patient validateDelete(long id){
        return patientRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Paciente não encontrada!"));
    }
}
