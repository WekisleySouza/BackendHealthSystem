package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.basic_requests.InstituitionRequestDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Instituition;
import com.project.healthsystem.repository.InstituitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstituitionValidator{
    private final InstituitionRepository instituitionRepository;

    public void validateSave(InstituitionRequestDTO instituitionRequestDTO){

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