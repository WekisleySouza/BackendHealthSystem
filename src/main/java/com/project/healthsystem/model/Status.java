package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.StatusDTO;
import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="tb_status")
@Data
@NoArgsConstructor
public class Status extends IDAbstraction {

    @Column(name="specification")
    private String specification;

    @OneToMany(mappedBy = "status")
    private List<Consultation> consultation;

    public Status(String specification){
        this.specification = specification;
    }

    public void coppingFromStatusDTO(StatusDTO statusDTO){
        this.specification = statusDTO.getSpecification();
    }
}
