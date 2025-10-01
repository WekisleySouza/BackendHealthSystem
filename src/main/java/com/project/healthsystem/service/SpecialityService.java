package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.SpecialityDTO;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.model.Speciality;
import com.project.healthsystem.repository.CategoryGroupRepository;
import com.project.healthsystem.repository.SpecialityRepository;
import com.project.healthsystem.validator.SpecialityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialityService {

    private final SpecialityRepository repository;
    private final CategoryGroupRepository categoryGroupRepository;
    private final SpecialityValidator specialityValidator;

    public Speciality save(SpecialityDTO specialityDTO){
        Speciality speciality = specialityDTO.mappingToSpeciality();
        Optional<CategoryGroup> categoryGroup = categoryGroupRepository.findById(specialityDTO.getCategoryGroupId());

        if(categoryGroup.isPresent()){
            speciality.setCategoryGroup(categoryGroup.get());
        }

        return repository.save(speciality);
    }

    public void update(SpecialityDTO specialityDTO, long id){
        specialityValidator.validate(id);
        Optional<Speciality> specialityOptional = repository.findById(id);
        Optional<CategoryGroup> categoryGroup = categoryGroupRepository.findById(specialityDTO.getCategoryGroupId());

        Speciality speciality = specialityOptional.get();
        speciality.coppingFromSpecialityDTO(specialityDTO);

        specialityValidator.validate(speciality);

        if(categoryGroup.isPresent()){
            speciality.setCategoryGroup(categoryGroup.get());
        }

        repository.save(speciality);
    }

    public List<SpecialityDTO> getAll(){
        return repository
            .findAll()
            .stream()
            .map(SpecialityDTO::new)
            .collect(Collectors.toList());
    }

    public Optional<Speciality> findById(long id){
        specialityValidator.validate(id);
        return this.repository.findById(id);
    }

    public void delete(long id){
        specialityValidator.validate(id);
        Optional<Speciality> specialityOptional = repository.findById(id);
        repository.delete(specialityOptional.get());
    }
}