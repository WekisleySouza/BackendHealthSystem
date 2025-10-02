package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.ServiceTypeDTO;
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
public class ServiceType extends IDAbstraction {
    @OneToMany(mappedBy = "serviceType")
    private List<Appointment> appointment;
    @ManyToOne
    private CategoryGroup categoryGroup;
    @Column(name = "specification", nullable = false)
    private String name;
    @Column(name = "value", precision = 20, scale = 8)
    private BigDecimal value;
    @Column(name = "type")
    private String type;

    public ServiceType(long id, String name, BigDecimal value, String type){
        this.id = id;
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public void coppingFromServiceTypesDTO(ServiceTypeDTO serviceTypeDTO){
        this.name = serviceTypeDTO.getName();
        this.value = serviceTypeDTO.getValue();
        this.type = serviceTypeDTO.getType();
    }

    public long getCategoryGroupId(){
        if (this.categoryGroup != null){
            return this.categoryGroup.getId();
        }
        return -1;
    }
}
