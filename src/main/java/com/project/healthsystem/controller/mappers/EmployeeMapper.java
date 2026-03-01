package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.EmployeeRequestDTO;
import com.project.healthsystem.controller.dto.EmployeeResponseDTO;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Gender;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Sex;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class EmployeeMapper {

    @Mapping(target = "person", expression = "java(map(dto))")
    public abstract Employee toEntity(EmployeeRequestDTO dto);

    @Mapping(target = "cpf", expression = "java(entity.getPerson().getCpf())")
    @Mapping(target = "name", expression = "java(entity.getPerson().getName())")
    @Mapping(target = "gender", expression = "java(entity.getPerson().getGender().getLabel())")
    @Mapping(target = "sex", expression = "java(entity.getPerson().getSex().getLabel())")
    @Mapping(target = "cellPhone", expression = "java(entity.getPerson().getCellPhone())")
    @Mapping(target = "residentialPhone", expression = "java(entity.getPerson().getResidentialPhone())")
    @Mapping(target = "contactPhone", expression = "java(entity.getPerson().getContactPhone())")
    @Mapping(target = "address", expression = "java(entity.getPerson().getAddress())")
    @Mapping(target = "birthday", expression = "java(entity.getPerson().getBirthday())")
    @Mapping(target = "email", expression = "java(entity.getPerson().getEmail())")
    @Mapping(target = "roles", expression = "java(entity.getPerson().getStringRoles())")
    public abstract EmployeeResponseDTO toDto(Employee entity);

    public Employee toEntityWhenHasId(Employee entity, EmployeeRequestDTO dto){
        entity.getPerson().setCpf(dto.getCpf());
        entity.getPerson().setName(dto.getName());
        entity.getPerson().setGender(Gender.fromLabel(dto.getGender()));
        entity.getPerson().setAddress(dto.getAddress());
        entity.getPerson().setSex(Sex.fromLabel(dto.getSex()));
        entity.getPerson().setCellPhone(dto.getCellPhone());
        entity.getPerson().setResidentialPhone(dto.getResidentialPhone());
        entity.getPerson().setContactPhone(dto.getContactPhone());
        entity.getPerson().setBirthday(dto.getBirthday());
        entity.getPerson().setEmail(dto.getEmail());
        return entity;
    }

    protected Person map(EmployeeRequestDTO dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setGender(Gender.fromLabel(dto.getGender()));
        person.setCpf(dto.getCpfNormalized());
        person.setAddress(dto.getAddress());
        person.setSex(Sex.fromLabel(dto.getSex()));
        person.setCellPhone(dto.getCellPhone());
        person.setResidentialPhone(dto.getResidentialPhone());
        person.setContactPhone(dto.getContactPhone());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        return person;
    }
}