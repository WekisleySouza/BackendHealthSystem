package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.BasicEntityAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="tb_agent")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Agent extends BasicEntityAbstraction {


    @OneToOne(optional = false)
    @JoinColumn(name = "person_id", unique = true)
    private Person person;
    @OneToMany(mappedBy = "agent")
    private List<Patient> patients;
}
