package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.BasicEntityAbstraction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "agreements")
public class Agreement extends BasicEntityAbstraction {
    @Column
    private String name;
}
