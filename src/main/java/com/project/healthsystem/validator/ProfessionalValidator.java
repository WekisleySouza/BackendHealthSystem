package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.ProfessionalRequestDTO;
import com.project.healthsystem.controller.mappers.ProfessionalMapper;
import com.project.healthsystem.exceptions.DuplicatedRegisterException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Professional;
import com.project.healthsystem.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfessionalValidator {
    private final ProfessionalRepository professionalRepository;
    private final ProfessionalMapper professionalMapper;

    public Professional validateSave(ProfessionalRequestDTO professionalRequestDTO){
        if (professionalRepository.existsByPersonCpf(professionalRequestDTO.getCpfNormalized())) {
            throw new DuplicatedRegisterException("Cpf já cadastrado para um professional!");
        }
        return professionalMapper.toEntity(professionalRequestDTO);
    }

    public Professional validateUpdate(ProfessionalRequestDTO professionalRequestDTO, long id){
        Professional professional = professionalRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Profissional não encontrado!"));
        return professionalMapper.toEntityWhenHasId(professional, professionalRequestDTO);
    }


    public Professional validateFindById(long id){
        return professionalRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Profissional não encontrado!"));
    }


    public Professional validateDelete(long id){
        return professionalRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Profissional não encontrado!"));
    }


    public void validate(long id){
        if(!exists(id)){
            throw new NotFoundException("Não foi encontrado professional com este id!");
        }
    }

    public boolean exists(long id){
        return professionalRepository.existsById(id);
    }
}
