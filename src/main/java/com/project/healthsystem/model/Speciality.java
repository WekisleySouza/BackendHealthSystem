package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.SpecialityDTO;
import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "tb_speciality")
@Data
@NoArgsConstructor
public class Speciality extends IDAbstraction {
    @OneToMany(mappedBy = "specialty")
    private List<Consultation> consultation;
    @ManyToOne
    private CategoryGroup categoryGroup;
    @Column(name = "specification", nullable = false)
    private String name;
    @Column(name = "value", precision = 20, scale = 8)
    private BigDecimal value;
    @Column(name = "is_exam")
    private boolean isExam;

    public Speciality(long id, String name, BigDecimal value, boolean isExam){
        this.id = id;
        this.name = name;
        this.value = value;
        this.isExam = isExam;
    }

    public void coppingFromSpecialityDTO(SpecialityDTO specialityDTO){
        this.name = specialityDTO.getName();
        this.value = specialityDTO.getValue();
        this.isExam = specialityDTO.isExam();
    }

    public long getCategoryGroupId(){
        if (this.categoryGroup != null){
            return this.categoryGroup.getId();
        }
        return -1;
    }
}
