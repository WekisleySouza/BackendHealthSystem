package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.EmployeeDTO;
import com.project.healthsystem.model.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class EmployeeMapper {

    public abstract Employee toEntity(EmployeeDTO dto);

    public abstract EmployeeDTO toDto(Employee entity);

    public Employee toEntityWhenHasId(Employee entity, EmployeeDTO dto){
        Employee newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}