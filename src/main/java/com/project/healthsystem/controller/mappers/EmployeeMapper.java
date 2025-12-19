package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.EmployeeRequestDTO;
import com.project.healthsystem.controller.dto.EmployeeResponseDTO;
import com.project.healthsystem.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class EmployeeMapper {

    @Mapping(target = "cpf", expression = "java(dto.getCpfNormalized())")
    public abstract Employee toEntity(EmployeeRequestDTO dto);

    public abstract EmployeeResponseDTO toDto(Employee entity);

    public Employee toEntityWhenHasId(Employee entity, EmployeeRequestDTO dto){
        entity.setName(dto.getName());
        entity.setCpf(dto.getCpf());
        entity.setBirthday(dto.getBirthday());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        return entity;
    }
}