package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.ConditionDTO;
import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="tb_condition")
@Data
@NoArgsConstructor
public class Condition extends IDAbstraction {

    @Column(name="specification")
    private String specification;

    @ManyToMany(mappedBy = "conditions")
    private List<Person> persons;

    public Condition(String specification){
        this.specification = specification;
    }

    public void coppingFromConditionDTO(ConditionDTO conditionDTO){
        this.specification = conditionDTO.getSpecification();
    }
}
