package com.project.healthsystem.service.datasus;

import com.project.healthsystem.controller.dto.basic_responses.CnesProfessionalResponseDTO;
import com.project.healthsystem.controller.mappers.CnesProfessionalMapper;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.repository.datasus.CnesProfessionalsRepository;
import com.project.healthsystem.repository.specs.CnesProfessionalSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CnesProfessionalService {
    private final CnesProfessionalsRepository repository;
    private final CnesProfessionalMapper mapper;

    @Transactional(readOnly = true)
    public Page<CnesProfessionalResponseDTO> findAll(int pageNumber, int pageLength, String search, String cnes, String cbo) {
        if (pageNumber < 0 || pageLength < 1 || pageLength > 100) {
            throw new InvalidDataException("Página inválida: use page-number >= 0 e page-length entre 1 e 100!");
        }
        return repository.findAll(CnesProfessionalSpecs.search(search, cnes, cbo),
            PageRequest.of(pageNumber, pageLength, Sort.by("name", "id").ascending())).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public CnesProfessionalResponseDTO findById(long id) {
        return mapper.toDto(repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Vínculo profissional CNES não encontrado!")));
    }
}
