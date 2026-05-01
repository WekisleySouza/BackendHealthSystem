package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.basic_requests.InstituitionRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.InstituitionResponseDTO;
import com.project.healthsystem.controller.mappers.InstituitionMapper;
import com.project.healthsystem.model.Instituition;
import com.project.healthsystem.repository.InstituitionRepository;
import com.project.healthsystem.repository.specs.InstituitionSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.validator.InstituitionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstituitionService {
    private final InstituitionRepository instituitionRepository;
    private final InstituitionValidator instituitionValidator;
    private final InstituitionMapper instituitionMapper;

    public InstituitionResponseDTO save(InstituitionRequestDTO instituitionRequestDTO){
        this.instituitionValidator.validateSave(instituitionRequestDTO);
        Instituition instituition = this.instituitionMapper.toEntity(instituitionRequestDTO);
        return this.instituitionMapper.toDto(this.instituitionRepository.save(instituition));
    }

    public void update(long id, InstituitionRequestDTO instituitionRequestDTO){
        Instituition instituition = this.instituitionValidator.validateUpdate(id);
        instituition = this.instituitionMapper.toEntityWhenHasId(instituition, instituitionRequestDTO);
        this.instituitionRepository.save(instituition);
    }

    public InstituitionResponseDTO findById(long id){
        Instituition instituition = this.instituitionValidator.validateFindById(id);
        return this.instituitionMapper.toDto(instituition);
    }

    public Page<InstituitionResponseDTO> findAll(
            Integer pageNumber,
            Integer pageLength,
            String name,
            String cep,
            String cityName,
            String address,
            String phone
    ){
        Sort sort = Sort.by("name").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        Specification<Instituition> specs = null;
        specs = SpecsCommon.addSpec(specs, InstituitionSpecs.nameLike(name));
        specs = SpecsCommon.addSpec(specs, InstituitionSpecs.cepLike(cep));
        specs = SpecsCommon.addSpec(specs, InstituitionSpecs.cityNameLike(cityName));
        specs = SpecsCommon.addSpec(specs, InstituitionSpecs.addressLike(address));
        specs = SpecsCommon.addSpec(specs, InstituitionSpecs.phoneLike(phone));
        return this.instituitionRepository
            .findAll(specs, pageRequest)
            .map(instituitionMapper::toDto);
    }

    public void delete(long id){
        Instituition instituition = this.instituitionValidator.validateDelete(id);
        this.instituitionRepository.delete(instituition);
    }
}
