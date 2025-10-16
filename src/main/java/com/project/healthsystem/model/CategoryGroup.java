package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tb_category_group")
@Data
@NoArgsConstructor
public class CategoryGroup extends IDAbstraction {

    @OneToMany(mappedBy = "categoryGroup")
    private List<ServiceType> serviceTypes;
    @Column(name = "name")
    private String name;

}
