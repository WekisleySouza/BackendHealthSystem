package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.SurgeryTypeDTO;
import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tb_surgery_type")
@Data
@NoArgsConstructor
public class SurgeryType extends IDAbstraction {

    @Column(name = "type")
    private String type;

    @OneToMany(mappedBy = "surgeryType")
    private List<Surgery> surgeries;

    public SurgeryType(String type){
        this.type = type;
    }

    public void coppingFromSurgeryTypeDTO(SurgeryTypeDTO surgeryTypeDTO){
        this.type = surgeryTypeDTO.getType();
    }
}
