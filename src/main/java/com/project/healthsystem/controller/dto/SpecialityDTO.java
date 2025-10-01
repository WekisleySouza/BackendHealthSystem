package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.model.Speciality;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialityDTO {

    private long id;
    private long categoryGroupId;

    private String name;
    private BigDecimal value;
    private boolean isExam;

    public SpecialityDTO(Speciality speciality){
        this.id = speciality.getId();
        this.categoryGroupId = speciality.getCategoryGroupId();

        this.name = speciality.getName();
        this.value = speciality.getValue();
        this.isExam = speciality.isExam();
    }

    public Speciality mappingToSpeciality(){
        return new Speciality(this.id, this.name, this.value, this.isExam);
    }

    public Speciality mappingToSpeciality(CategoryGroup categoryGroup){
        Speciality speciality = new Speciality(
                this.id,
                this.name,
                this.value,
                this.isExam
        );
        speciality.setCategoryGroup(categoryGroup);
        return speciality;
    }
}
