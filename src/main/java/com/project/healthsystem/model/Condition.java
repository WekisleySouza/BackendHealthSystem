package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.BasicEntityAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="tb_condition")
@Data
@NoArgsConstructor
public class Condition extends BasicEntityAbstraction {

    @Column(name="specification")
    private String specification;

    @ManyToMany(mappedBy = "conditions")
    private List<Person> persons;

}
