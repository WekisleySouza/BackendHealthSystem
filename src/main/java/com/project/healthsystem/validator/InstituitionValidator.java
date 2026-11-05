package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.basic_requests.InstituitionRequestDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.model.Instituition;
import com.project.healthsystem.repository.InstituitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstituitionValidator{
    private final InstituitionRepository instituitionRepository;

    public void validateSave(InstituitionRequestDTO instituitionRequestDTO){
        validateCnes(instituitionRequestDTO.getCnes());
    }

    public void validateCnes(String cnes) {
        if (cnes != null && !cnes.isBlank() && !cnes.matches("[0-9]{7}")) {
            throw new InvalidDataException("O CNES deve conter exatamente 7 dígitos.");
        }
    }

    public Instituition validateUpdate(long id){
        return this.instituitionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Não foi possivel atualizar instituição!"));
    }

    public Instituition validateFindById(long id){
        return this.instituitionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Não foi possivel encontrar instituição!"));
    }

    public Instituition validateDelete(long id){
        return this.instituitionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Não foi possivel encontrar instituição!"));
    }

}