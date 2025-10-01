package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Agent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class AgentDTO {

    private long id;
    private String name;
    private String cpf;
    private String phone;
    private String birthday;
    private String email;

    public AgentDTO(Agent agent){
        this.id = agent.getId();
        this.name = agent.getName();
        this.cpf = agent.getCpf();
        this.phone = agent.getPhone();
        this.birthday = agent.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.email = agent.getEmail();
    }

    public Agent mappingToAgent(){
        Agent agent = new Agent();

        agent.setEmail(this.email);
        agent.setBirthday(LocalDate.parse(this.birthday, DateTimeFormatter.ofPattern("dd/MM/yyy")));
        agent.setCpf(this.cpf);
        agent.setPhone(this.phone);
        agent.setName(this.name);

        return agent;
    }
}
