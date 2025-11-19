package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.SurgeryRequestDTO;
import com.project.healthsystem.controller.mappers.SurgeryMapper;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Surgery;
import com.project.healthsystem.model.SurgeryType;
import com.project.healthsystem.repository.SurgeryRepository;
import com.project.healthsystem.repository.SurgeryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurgeryValidator {
    private final SurgeryRepository surgeryRepository;
    private final SurgeryTypeRepository surgeryTypeRepository;
    private final SurgeryMapper surgeryMapper;

    public Surgery validateSave(SurgeryRequestDTO surgeryRequestDTO){
        SurgeryType surgeryType = surgeryTypeRepository
            .findById(surgeryRequestDTO.getSurgeryTypeId())
            .orElseThrow(() -> new NotFoundException("Tipo de cirurgia não encontrado!"));
        Surgery surgery = surgeryMapper.toEntity(surgeryRequestDTO);
        surgery.setSurgeryType(surgeryType);
        return surgery;
    }

    public Surgery validateUpdate(SurgeryRequestDTO surgeryRequestDTO, long id){
        Surgery surgery = surgeryRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Cirurgia não encontrada!"));
        SurgeryType surgeryType = surgeryTypeRepository
            .findById(surgeryRequestDTO.getSurgeryTypeId())
            .orElseThrow(() -> new NotFoundException("Tipo de cirurgia não encontrado!"));
        surgery = surgeryMapper.toEntityWhenHasId(surgery, surgeryRequestDTO);
        surgery.setSurgeryType(surgeryType);
        return surgery;
    }

    public Surgery validateFindById(long id){
        return surgeryRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Cirurgia não encontrada!"));
    }

    public Surgery validateDelete(long id){
        return surgeryRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Cirurgia não encontrada!"));
    }
}
