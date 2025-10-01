package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.PersonDTO;
import com.project.healthsystem.model.abstractions.UserBasicAbstraction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tb_person")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Person extends UserBasicAbstraction {

    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private Person responsible;
    @ManyToMany
    @JoinTable(
        name = "person_condition",
        joinColumns = @JoinColumn(name = "person_id"),
        inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    private List<Condition> conditions;
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;
    @OneToOne(mappedBy = "person")
    private Login login;
    @OneToMany(mappedBy = "person")
    private List<Consultation> consultations;

    @Column(name = "cns", nullable = false)
    private String cns;
    @Column(name = "mother_name")
    private String motherName;

    public void coppingFromPersonDto(PersonDTO personDto){

        this.cpf = personDto.getCpf();
        this.birthday = LocalDate.parse(
                personDto.getBirthday(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
        this.motherName = personDto.getMotherName();
        this.name = personDto.getName();
        this.cns = personDto.getCns();
        this.email = personDto.getEmail();
    }

    public long getAgentId(){
        if(agent != null){
            return  agent.getId();
        }
        return -1;
    }

    public List<Long> getConditionsId(){
        if(this.conditions != null){
            List<Long> ids = new ArrayList<>();
            for(Condition condition : this.conditions){
                ids.add(condition.getId());
            }
            return ids;
        }
        return null;
    }
}
