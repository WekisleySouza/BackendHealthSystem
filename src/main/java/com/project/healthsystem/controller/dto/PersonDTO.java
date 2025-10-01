package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    private long id;
    private long agentId;
    private List<Long> conditionsId;

    private String name;
    private String motherName;
    private String birthday;
    private String cns;
    private String cpf;
    private String phone;
    private String email;

    public PersonDTO(String name, LocalDate birthday, String cns, String cpf){
        this.name = name;
        this.birthday = birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.cns = cns;
        this.cpf = cpf;
    }

    public PersonDTO(Person person){
        this.id = person.getId();
        this.conditionsId = person.getConditionsId();

        this.agentId = person.getAgentId();

        this.name = person.getName();
        this.birthday = person.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.cns = person.getCns();
        this.cpf = person.getCpf();
        this.motherName = person.getMotherName();
        this.phone = person.getPhone();
    }

    public Person mappingToPerson(){
        Person person = new Person();

        person.setName(this.name);
        person.setBirthday(LocalDate.parse(this.birthday, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        person.setCns(this.cns);
        person.setCpf(this.cpf);
        person.setMotherName(this.motherName);
        person.setPhone(this.phone);
        person.setEmail(this.email);

        return person;
    }

    public Person mappingToPerson(Agent agent){
        Person person = new Person();

        person.setName(this.name);
        person.setBirthday(LocalDate.parse(this.birthday, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        person.setCns(this.cns);
        person.setCpf(this.cpf);
        person.setMotherName(this.motherName);
        person.setPhone(this.phone);
        person.setEmail(this.email);
        person.setAgent(agent);

        return person;
    }
}
