package com.project.healthsystem.service.datasus;

import com.project.healthsystem.controller.dto.basic_responses.Cid10ResponseDTO;
import com.project.healthsystem.controller.mappers.Cid10Mapper;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.repository.datasus.Cid10Repository;
import com.project.healthsystem.repository.specs.Cid10Specs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Cid10Service {
    private final Cid10Repository repository;
    private final Cid10Mapper mapper;

    @Transactional(readOnly = true)
    public Page<Cid10ResponseDTO> findAll(int pageNumber, int pageLength, String search) {
        if (pageNumber < 0 || pageLength < 1 || pageLength > 100) {
            throw new InvalidDataException(
                "Página inválida: use page-number >= 0 e page-length entre 1 e 100!");
        }
        var pageable = PageRequest.of(pageNumber, pageLength, Sort.by("code").ascending());
        return repository.findAll(Cid10Specs.search(search), pageable).map(mapper::toDto);
    }
}
