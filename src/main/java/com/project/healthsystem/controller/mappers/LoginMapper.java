package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.LoginDTO;
import com.project.healthsystem.model.Login;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class LoginMapper {

    public abstract Login toEntity(LoginDTO dto);

    @Mapping(target = "userId", expression = "java(this.getUserId(entity))")
    public abstract LoginDTO toDto(Login entity);

    protected Long getUserId(Login entity) {
        return entity.getPersonId() != -1
                ? entity.getPersonId()
                : entity.getEmployeeId();
    }

    public Login toEntityWhenHasId(Login entity, LoginDTO dto){
        Login newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}
