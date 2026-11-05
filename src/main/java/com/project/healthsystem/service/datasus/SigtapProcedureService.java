package com.project.healthsystem.service.datasus;

import com.project.healthsystem.controller.dto.basic_responses.SigtapProcedureResponseDTO;
import com.project.healthsystem.controller.mappers.SigtapProcedureMapper;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.repository.datasus.SigtapProcedureRepository;
import com.project.healthsystem.repository.specs.SigtapProcedureSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SigtapProcedureService {
    private final SigtapProcedureRepository repository;
    private final SigtapProcedureMapper mapper;

    @Transactional(readOnly = true)
    public Page<SigtapProcedureResponseDTO> findAll(int pageNumber, int pageLength, String search) {
        if (pageNumber < 0 || pageLength < 1 || pageLength > 100) {
            throw new InvalidDataException("Página inválida: use page-number >= 0 e page-length entre 1 e 100!");
        }
        return repository.findAll(SigtapProcedureSpecs.search(search),
            PageRequest.of(pageNumber, pageLength, Sort.by("code", "id").ascending()))
            .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public SigtapProcedureResponseDTO findById(long id) {
        return mapper.toDto(repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Procedimento SIGTAP não encontrado!")));
    }
}
