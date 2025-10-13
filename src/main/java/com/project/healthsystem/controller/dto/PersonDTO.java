package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Person;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    private long id;

    @NotNull(message = "O usuário deve estar atribuído a um agente!")
    private Long agentId;

    private Long responsibleId;

    private List<Long> conditionsId;

    @NotNull(message = "O nome é obrigatório!")
    private String name;

    private String motherName;

    @NotNull(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

    @NotNull(message = "O CNS é obrigatório!")
    private String cns;

    @NotNull(message = "O cartão SUS é obrigatório!")
    private String susId;

    @NotNull(message = "O CPF é obrigatório!")
    @CPF(message = "CPF inválido!")
    private String cpf;

    private String phone;

    @Email(message = "Formato de e-mail inválido!")
    private String email;

    public PersonDTO(String name, LocalDate birthday, String cns, String cpf){
        this.name = name;
        this.birthday = birthday;
        this.cns = cns;
        this.cpf = cpf;
    }

    public PersonDTO(Person person){
        this.id = person.getId();
        this.conditionsId = person.getConditionsId();

        this.agentId = person.getAgentId();

        this.name = person.getName();
        this.birthday = person.getBirthday();
        this.cns = person.getCns();
        this.susId = person.getSusId();
        this.cpf = person.getCpf();
        this.motherName = person.getMotherName();
        this.phone = person.getPhone();
    }

    public Person mappingToPerson(){
        Person person = new Person();

        person.setName(this.name);
        person.setBirthday(this.birthday);
        person.setCns(this.cns);
        person.setSusId(this.susId);
        person.setCpf(this.cpf);
        person.setMotherName(this.motherName);
        person.setPhone(this.phone);
        person.setEmail(this.email);

        return person;
    }

    public Person mappingToPerson(Agent agent){
        Person person = new Person();

        person.setName(this.name);
        person.setBirthday(this.birthday);
        person.setCns(this.cns);
        person.setSusId(this.susId);
        person.setCpf(this.cpf);
        person.setMotherName(this.motherName);
        person.setPhone(this.phone);
        person.setEmail(this.email);
        person.setAgent(agent);

        return person;
    }
}
