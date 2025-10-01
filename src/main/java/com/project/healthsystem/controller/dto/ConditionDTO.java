package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Condition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConditionDTO {
    private long id;
    private String specification;

    public ConditionDTO(Condition condition){
        this.id = condition.getId();
        this.specification = condition.getSpecification();
    }

    public Condition mappingToCondition(){
        return new Condition(this.specification);
    }
}
