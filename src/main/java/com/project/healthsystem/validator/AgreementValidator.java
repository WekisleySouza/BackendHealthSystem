package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.repository.AgreementRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgreementValidator {
    private final AgreementRepository agreementRepository;

    public AgreementValidator idHasToExist(Long id){
        if(!agreementRepository.existsById(id)){
            throw new InvalidDataException("Este id é inválido!!");
        }
        return this;
    }

    public AgreementValidator nameCanNotExist(String name){
        if(!agreementRepository.existsByName(name)){
            throw new InvalidDataException("Este nome é inválido!!");
        }
        return this;
    }
}
