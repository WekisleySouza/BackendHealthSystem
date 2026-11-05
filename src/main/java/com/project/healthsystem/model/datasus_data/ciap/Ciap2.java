package com.project.healthsystem.model.datasus_data.ciap;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_ciap2")
@Data
@NoArgsConstructor
public class Ciap2 extends IDAbstraction {

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "pec_id")
    private Long pecId;

    private String sex;

    @Column(name = "default_filter_in_pec")
    private Boolean defaultFilterInPec;
}
