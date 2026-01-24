package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.EmployeeRequestDTO;
import com.project.healthsystem.controller.dto.EmployeeResponseDTO;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class EmployeeMapper {

    @Mapping(target = "person", expression = "java(map(dto))")
    public abstract Employee toEntity(EmployeeRequestDTO dto);

    @Mapping(target = "cpf", expression = "java(entity.getPerson().getCpf())")
    @Mapping(target = "name", expression = "java(entity.getPerson().getName())")
    @Mapping(target = "phone", expression = "java(entity.getPerson().getPhone())")
    @Mapping(target = "address", expression = "java(entity.getPerson().getAddress())")
    @Mapping(target = "birthday", expression = "java(entity.getPerson().getBirthday())")
    @Mapping(target = "email", expression = "java(entity.getPerson().getEmail())")
    public abstract EmployeeResponseDTO toDto(Employee entity);

    public Employee toEntityWhenHasId(Employee entity, EmployeeRequestDTO dto){
        entity.getPerson().setCpf(dto.getCpf());
        entity.getPerson().setName(dto.getName());
        entity.getPerson().setAddress(dto.getAddress());
        entity.getPerson().setPhone(dto.getPhone());
        entity.getPerson().setBirthday(dto.getBirthday());
        entity.getPerson().setEmail(dto.getEmail());
        return entity;
    }

    protected Person map(EmployeeRequestDTO dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setAddress(dto.getAddress());
        person.setPhone(dto.getPhone());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        return person;
    }
}