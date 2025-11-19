package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.SurgeryTypeRequestDTO;
import com.project.healthsystem.controller.mappers.SurgeryTypeMapper;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.SurgeryType;
import com.project.healthsystem.repository.SurgeryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurgeryTypeValidator {
    private final SurgeryTypeRepository surgeryTypeRepository;
    private final SurgeryTypeMapper surgeryTypeMapper;

    public SurgeryType validateSave(SurgeryTypeRequestDTO surgeryTypeRequestDTO){
        return surgeryTypeMapper.toEntity(surgeryTypeRequestDTO);
    }

    public SurgeryType validateUpdate(SurgeryTypeRequestDTO surgeryTypeRequestDTO, long id){
        SurgeryType surgeryType = surgeryTypeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Tipo de cirurgia não encontrado!"));
        surgeryType = surgeryTypeMapper.toEntityWhenHasId(surgeryType, surgeryTypeRequestDTO);
        return surgeryType;
    }

    public SurgeryType validateFindById(long id){
        return surgeryTypeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Tipo de cirurgia não encontrado!"));
    }

    public SurgeryType validateDelete(long id){
        return surgeryTypeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Tipo de cirurgia não encontrado!"));
    }
}
