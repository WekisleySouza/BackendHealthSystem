package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.BasicEntityAbstraction;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tb_patient")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Patient extends BasicEntityAbstraction {

    // Only report fields
    @Column(name = "team_name")
    private String teamName;
    @Column(name = "team_ine")
    private String teamINE;
    @Column(name = "micro_area")
    private String microArea;
    @Column(name = "origin")
    private String origin;

    // System fields
    @Column(name = "cns")
    private String cns;
    @Column(name = "mother_name")
    private String motherName;

    @OneToOne(optional = false)
    @JoinColumn(name = "person_id", unique = true)
    private Person person;
    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private Patient responsible;
    @ManyToMany
    @JoinTable(
        name = "patient_condition",
        joinColumns = @JoinColumn(name = "patient_id"),
        inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    private List<Condition> conditions;
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;
    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments;

    public long getAgentId(){
        if(agent != null){
            return  agent.getId();
        }
        return -1;
    }

    public long getResponsibleId(){
        if(this.responsible != null){
            return  this.responsible.getId();
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
