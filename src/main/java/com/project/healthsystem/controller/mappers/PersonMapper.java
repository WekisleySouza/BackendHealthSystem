package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.AgentRequestDTO;
import com.project.healthsystem.controller.dto.EmployeeRequestDTO;
import com.project.healthsystem.controller.dto.PatientRequestDTO;
import com.project.healthsystem.controller.dto.ProfessionalRequestDTO;
import com.project.healthsystem.model.Person;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class PersonMapper {
    public Person toPersonEntity(AgentRequestDTO dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        person.setAddress(dto.getAddress());
        return person;
    }

    public Person updatePersonEntity(Person person, AgentRequestDTO dto){
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        person.setAddress(dto.getAddress());
        return person;
    }

    public Person toPersonEntity(ProfessionalRequestDTO dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        person.setAddress(dto.getAddress());
        return person;
    }

    public Person updatePersonEntity(Person person, ProfessionalRequestDTO dto){
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        person.setAddress(dto.getAddress());
        return person;
    }

    public Person toPersonEntity(PatientRequestDTO dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        person.setAddress(dto.getAddress());
        return person;
    }

    public Person updatePersonEntity(Person person, PatientRequestDTO dto){
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        person.setAddress(dto.getAddress());
        return person;
    }

    public Person toPersonEntity(EmployeeRequestDTO dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        person.setAddress(dto.getAddress());
        return person;
    }

    public Person updatePersonEntity(Person person, EmployeeRequestDTO dto){
        person.setName(dto.getName());
        person.setCpf(dto.getCpfNormalized());
        person.setBirthday(dto.getBirthday());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        person.setAddress(dto.getAddress());
        return person;
    }
}
