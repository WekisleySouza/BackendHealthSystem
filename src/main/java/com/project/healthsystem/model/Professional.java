package com.project.healthsystem.model;


import com.project.healthsystem.model.abstractions.BasicEntityAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="tb_professional")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Professional extends BasicEntityAbstraction {


    @Column(name = "cns", length = 100)
    private String cns;
    @Column(name = "cbo", length = 100)
    private String cbo;
    @Column(name = "vinculation", length = 100)
    private String vinculation;
    @Column(name = "description", length = 100)
    private String description;

    @OneToOne(optional = false)
    @JoinColumn(name = "person_id", unique = true)
    private Person person;
    @OneToMany(mappedBy = "professional")
    List<Appointment> appointment;
}
