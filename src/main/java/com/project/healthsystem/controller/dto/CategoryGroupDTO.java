package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.CategoryGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryGroupDTO {

    private long id;
    private String name;

    public CategoryGroupDTO(CategoryGroup categoryGroup){
        this.id = categoryGroup.getId();
        this.name = categoryGroup.getName();
    }

    public CategoryGroup mappingToCategoryGroup(){
        return new CategoryGroup(this.name);
    }
}
