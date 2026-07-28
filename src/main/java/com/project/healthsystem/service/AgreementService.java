package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.basic_requests.AgreementRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AgreementResponseDTO;
import com.project.healthsystem.controller.mappers.AgreementMapper;
import com.project.healthsystem.model.Agreement;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.AgreementRepository;
import com.project.healthsystem.repository.specs.AgreementSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.AgreementValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgreementService {

    private final AgreementRepository agreementRepository;
    private final AgreementValidator agreementValidator;
    private final AgreementMapper agreementMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public Page<AgreementResponseDTO> getAll(
        Integer pageNumber,
        Integer pageLength,
        String name
    ){
        Sort sort = Sort.by("name").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        Specification<Agreement> specs = null;
        specs = SpecsCommon.addSpec(specs, AgreementSpecs.nameLike(name));
        return agreementRepository
            .findAll(specs, pageRequest)
            .map(agreementMapper::toDto);
    }

    public AgreementResponseDTO getById(Long id){
        agreementValidator.idHasToExist(id);
        return agreementMapper.toDto(agreementRepository.findById(id).orElse(null));
    }

    public AgreementResponseDTO save(AgreementRequestDTO agreementRequestDTO, String token){
        agreementValidator.nameCanNotExist(agreementRequestDTO.getName());
        Agreement agreement = agreementMapper.toEntity(agreementRequestDTO);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        agreement.createdNow();
        agreement.setCreatedBy(currentEditor);
        agreement.setLastModifiedBy(currentEditor);

        return agreementMapper.toDto(agreementRepository.save(agreement));
    }

    public void update(AgreementRequestDTO agreementRequestDTO, Long id, String token){
        agreementValidator
            .idHasToExist(id)
            .nameCanNotExist(agreementRequestDTO.getName());

        Agreement agreement = agreementRepository
            .findById(id)
            .orElse(null);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        agreement.setLastModifiedBy(currentEditor);
        agreement.updatedNow();

        agreementRepository.save(
            agreementMapper.toEntityWhenHasId(agreement, agreementRequestDTO)
        );
    }

    public void delete(long id){
        agreementValidator.idHasToExist(id);
        agreementRepository.deleteById(id);
    }
}
