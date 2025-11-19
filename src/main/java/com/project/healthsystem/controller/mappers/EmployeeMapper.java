package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.EmployeeRequestDTO;
import com.project.healthsystem.model.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class EmployeeMapper {

    public abstract Employee toEntity(EmployeeRequestDTO dto);

    public abstract EmployeeRequestDTO toDto(Employee entity);

    public Employee toEntityWhenHasId(Employee entity, EmployeeRequestDTO dto){
        Employee newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}