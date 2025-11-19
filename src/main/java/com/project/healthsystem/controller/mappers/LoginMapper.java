package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.LoginRequestDTO;
import com.project.healthsystem.model.Login;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class LoginMapper {

    public abstract Login toEntity(LoginRequestDTO dto);

    @Mapping(target = "personId", expression = "java(entity.getPersonId())")
    @Mapping(target = "employeeId", expression = "java(entity.getEmployeeId())")
    public abstract LoginRequestDTO toDto(Login entity);

    public Login toEntityWhenHasId(Login entity, LoginRequestDTO dto){
        Login newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}
